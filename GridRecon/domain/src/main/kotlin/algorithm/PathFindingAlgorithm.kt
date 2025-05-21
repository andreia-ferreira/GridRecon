package net.penguin.domain.algorithm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import net.penguin.domain.entity.DronePosition
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation

object PathFindingAlgorithm: PathFindingAlgorithmInterface {
    private const val BEAM_WIDTH = 10

    override fun run(simulation: Simulation): Flow<SearchState> = flow {
        val maxSteps = simulation.moves
        val startTime = System.currentTimeMillis()

        var currentBeam = listOf(
            DronePosition(
                position = simulation.startPosition,
                timeStep = 0,
                score = 0,
                cumulativeScore = 0,
                path = listOf(simulation.startPosition),
                gridSnapshot = simulation.grid.clone()
            )
        )

        var bestResult = currentBeam.first()
        var currentStep = 0

        emit(SearchState.Begin)

        while (currentBeam.isNotEmpty() && currentStep < maxSteps) {
            println("Time step: $currentStep")
            if (System.currentTimeMillis() - startTime >= simulation.maxDuration) break

            val candidates = mutableListOf<Pair<DronePosition, Int>>()

            for (dronePosition in currentBeam) {
                val neighbors = dronePosition.position.getNeighbors().filter {
                    simulation.grid.isValidPosition(it)
                }

                for (neighbor in neighbors) {
                    val nextStep = dronePosition.timeStep + 1
                    if (nextStep > maxSteps) continue

                    // Clone grid snapshot for simulation of consumption and regeneration
                    val gridSnapshot = dronePosition.gridSnapshot.clone()

                    // Calculate score and simulate consumption on the grid snapshot
                    val consumedValue = gridSnapshot.getCell(neighbor).consume(nextStep)
                    gridSnapshot.regenerateCells(nextStep)

                    val cumulativeScore = dronePosition.cumulativeScore + consumedValue
                    val path = dronePosition.path + neighbor

                    val estimatedTotalScore = cumulativeScore + estimatePotential(
                        this,
                        gridSnapshot,
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
                        gridSnapshot = gridSnapshot
                    )

                    candidates.add(nextDronePosition to estimatedTotalScore)

                    emit(SearchState.AddCandidate(nextDronePosition))
                }
            }

            // Keep top BEAM_WIDTH candidates
            val sortedCandidates = candidates.sortedByDescending { it.second }.take(BEAM_WIDTH)
            currentBeam = sortedCandidates.map { it.first }

            // Update best result
            val bestInBeam = currentBeam.maxByOrNull { it.cumulativeScore }
            if (bestInBeam != null && bestInBeam.cumulativeScore > bestResult.cumulativeScore) {
                bestResult = bestInBeam
                emit(SearchState.AddToBestOption(bestResult))
            }

            currentStep++
            emit(SearchState.Move(bestResult))
        }

        emit(SearchState.Result(bestResult.path, bestResult.cumulativeScore, bestResult.gridSnapshot))
    }

    private suspend fun estimatePotential(
        flowCollector: FlowCollector<SearchState>,
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

            totalEstimate += grid.getCell(best).getValue()
            current = best
            currentStep++
            remainingSteps--
            flowCollector.emit(SearchState.EvaluatingPotential(grid, from, neighbors, current))
        }

        return totalEstimate
    }

}