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
        simulation.startTimer()

        var currentBeam = listOf(
            DronePosition(
                position = simulation.startPosition,
                currentTurn = 0,
                score = 0,
                cumulativeScore = 0,
                path = listOf(simulation.startPosition),
            )
        )

        var bestResult = currentBeam.first()

        onStep(SearchState.Begin)

        while (currentBeam.isNotEmpty() && !simulation.isMovementLimitReached() && !simulation.isTimeLimitReached()) {
            println("Turn: ${simulation.currentTurn}")
            simulation.grid.regenerateCells(simulation.currentTurn)

            val candidates = mutableListOf<Pair<DronePosition, Int>>()

            for (dronePosition in currentBeam) {
                val neighbors = dronePosition.position.getNeighbors().filter {
                    simulation.grid.isValidPosition(it)
                }

                for (neighbor in neighbors) {
                    val nextStep = dronePosition.currentTurn + 1
                    if (nextStep > simulation.maxMoves) continue

                    val cellValue = simulation.grid.getCell(neighbor).getValue()
                    val cumulativeScore = dronePosition.cumulativeScore + cellValue
                    val path = dronePosition.path + neighbor

                    val estimatedTotalScore = cumulativeScore + estimatePotential(
                        onEvaluate = onStep,
                        grid = simulation.grid,
                        from = neighbor,
                        turn = nextStep,
                        stepsLeft = simulation.maxMoves - nextStep
                    )

                    val nextDronePosition = DronePosition(
                        position = neighbor,
                        currentTurn = nextStep,
                        score = cellValue,
                        cumulativeScore = cumulativeScore,
                        path = path,
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

                simulation.grid.getCell(bestInBeam.position).consume(simulation.currentTurn)
                onStep(SearchState.Move(it))
            }
            simulation.nextTurn()
        }

        val result = SearchState.Result(bestResult.path, bestResult.cumulativeScore, simulation.grid)
        onStep(result)
        return result
    }

    private fun estimatePotential(
        onEvaluate: (SearchState) -> Unit,
        grid: Grid,
        from: Position,
        turn: Int,
        stepsLeft: Int
    ): Int {
        var currentPosition = from
        var currentStep = turn
        var totalEstimate = 0
        var remainingSteps = stepsLeft

        while (remainingSteps > 0) {
            val neighbors = currentPosition.getNeighbors().filter { grid.isValidPosition(it) }

            val best = neighbors.maxByOrNull { position: Position ->
                val arrivalTime = currentStep + 1
                grid.estimateValueAt(position, arrivalTime)
            } ?: break

            totalEstimate += grid.estimateValueAt(best, currentStep + 1)
            currentPosition = best
            currentStep++
            remainingSteps--
            onEvaluate(SearchState.EvaluatingPotential(from, neighbors, currentPosition, grid))
        }

        return totalEstimate
    }
}