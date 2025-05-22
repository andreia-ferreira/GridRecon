package net.penguin.domain.algorithm

import net.penguin.domain.entity.DronePosition
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation

object DroneMovementBeamAlgorithm: DroneMovementAlgorithmInterface {
    private const val BEAM_WIDTH = 10

    override fun run(
        simulation: Simulation,
        onStep: (SearchState) -> Unit
    ): SearchState.Result {
        val maxSteps = simulation.moves
        val startTime = System.currentTimeMillis()

        var currentBeam = listOf(
            DronePosition(
                position = simulation.startPosition,
                timeStep = 0,
                score = 0,
                cumulativeScore = 0,
                path = listOf(simulation.startPosition),
                gridSnapshot = simulation.grid
            )
        )

        var bestResult = currentBeam.first()
        var currentStep = 0

        onStep(SearchState.Begin)

        while (currentBeam.isNotEmpty() && currentStep < maxSteps) {
            println("Time step: $currentStep")
            if (System.currentTimeMillis() - startTime >= simulation.maxDuration) break
            simulation.grid.regenerateCells(currentStep)

            val candidates = mutableListOf<Pair<DronePosition, Int>>()

            for (dronePosition in currentBeam) {
                val neighbors = dronePosition.position.getNeighbors().filter {
                    simulation.grid.isValidPosition(it)
                }

                for (neighbor in neighbors) {
                    val nextStep = dronePosition.timeStep + 1
                    if (nextStep > maxSteps) continue

                    val consumedValue = simulation.grid.getCell(neighbor).getValue()

                    val cumulativeScore = dronePosition.cumulativeScore + consumedValue
                    val path = dronePosition.path + neighbor

                    val estimatedTotalScore = cumulativeScore + estimatePotential(
                        onStep,
                        simulation.grid,
                        neighbor,
                        nextStep,
                        maxSteps - nextStep
                    )

                    val nextDronePosition = DronePosition(
                        position = neighbor,
                        timeStep = nextStep,
                        score = consumedValue,
                        cumulativeScore = cumulativeScore,
                        path = path,
                        gridSnapshot = simulation.grid
                    )

                    candidates.add(nextDronePosition to estimatedTotalScore)

                    onStep(SearchState.AddCandidate(nextDronePosition))
                }
            }

            // Keep top BEAM_WIDTH candidates
            val sortedCandidates = candidates.sortedByDescending { it.second }.take(BEAM_WIDTH)
            currentBeam = sortedCandidates.map { it.first }

            // Update best result
            val bestInBeam = currentBeam.maxByOrNull { it.cumulativeScore }
            bestInBeam?.let {
                if (bestInBeam.cumulativeScore > bestResult.cumulativeScore) {
                    bestResult = bestInBeam
                    onStep(SearchState.AddToBestOption(bestResult))
                }

                simulation.grid.getCell(bestInBeam.position).consume(currentStep)
                onStep(SearchState.Move(it))
            }
            currentStep++
        }

        val result = SearchState.Result(bestResult.path, bestResult.cumulativeScore, bestResult.gridSnapshot)
        onStep(result)
        return result
    }

    private fun estimatePotential(
        onEvaluate: (SearchState) -> Unit,
        grid: Grid,
        from: Position,
        timeStep: Int,
        stepsLeft: Int
    ): Int {
        var current = from
        var currentStep = timeStep
        var totalEstimate = 0
        var remainingSteps = stepsLeft

        while (remainingSteps > 0) {
            val neighbors = current.getNeighbors().filter { grid.isValidPosition(it) }

            val best = neighbors.maxByOrNull { position: Position ->
                val arrivalTime = currentStep + 1
                grid.estimateValueAt(position, arrivalTime)
            } ?: break

            totalEstimate += grid.estimateValueAt(best, currentStep + 1)
            current = best
            currentStep++
            remainingSteps--
            onEvaluate(SearchState.EvaluatingPotential(from, neighbors, current, grid))
        }

        return totalEstimate
    }

}