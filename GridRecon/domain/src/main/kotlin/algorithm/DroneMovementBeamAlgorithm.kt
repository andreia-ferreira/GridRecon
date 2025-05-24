package net.penguin.domain.algorithm

import net.penguin.domain.entity.Drone
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation

@JvmInline
value class PotentialScore(val value: Int)
private typealias Candidate = Pair<Drone.Move, PotentialScore>

object DroneMovementBeamAlgorithm: DroneMovementAlgorithmInterface {
    private const val BEAM_WIDTH = 10

    override fun run(
        simulation: Simulation,
        onStep: (SearchState) -> Unit
    ) {
        simulation.startTimer()

        var currentBeam = listOf(simulation.drone.getAllMovesData().first())

        onStep(SearchState.Begin)

        while (currentBeam.isNotEmpty() && !simulation.isMovementLimitReached() && !simulation.isTimeLimitReached()) {
            val candidates = expandBeam(
                currentBeam = currentBeam,
                simulation = simulation,
                onStep = onStep
            )
            if (candidates.isEmpty()) break

            // Keep top BEAM_WIDTH candidates
            val sortedCandidates = candidates
                .sortedWith(compareByDescending<Candidate> { it.second.value }
                    .thenByDescending { it.first.score })
                .take(BEAM_WIDTH)

            val bestNextMove = sortedCandidates
                .map { it.first }
                .firstOrNull {
                    it.turn == simulation.currentTurn + 1 && it.position.isNeighbor(simulation.drone.getCurrentPosition())
                }

            if (bestNextMove == null) break

            simulation.moveDrone(bestNextMove)
            currentBeam = listOf(bestNextMove)
            onStep(SearchState.Move)

            simulation.nextTurn()
        }

        val result = SearchState.Result
        onStep(result)
    }

    private fun expandBeam(
        currentBeam: List<Drone.Move>,
        simulation: Simulation,
        onStep: (SearchState) -> Unit
    ): List<Candidate> {
        val candidates = mutableListOf<Candidate>()
        val nextTurn = simulation.currentTurn + 1

        for (potentialPos in currentBeam) {
            val neighbors = potentialPos.position.getNeighbors().filter {
                simulation.grid.isValidPosition(it)
            }

            for (neighbor in neighbors) {
                if (nextTurn > simulation.maxMoves) continue

                val cellValue = simulation.grid.getCell(neighbor).getValue()
                val cumulativeScore = potentialPos.cumulativeScore + cellValue

                val estimatedTotalScore = cumulativeScore + estimatePotential(
                    onEvaluate = onStep,
                    grid = simulation.grid,
                    from = neighbor,
                    turn = nextTurn,
                    stepsLeft = simulation.maxMoves - nextTurn
                )

                val nextDronePosition = Drone.Move(
                    position = neighbor,
                    turn = nextTurn,
                    score = cellValue,
                    parent = potentialPos,
                    cumulativeScore = cumulativeScore
                )

                candidates.add(nextDronePosition to PotentialScore(estimatedTotalScore))
                onStep(SearchState.AddCandidate(nextDronePosition))
            }
        }
        return candidates
    }

    private fun estimatePotential(
        onEvaluate: (SearchState) -> Unit,
        grid: Grid,
        from: Position,
        turn: Int,
        stepsLeft: Int
    ): Int {
        var totalScore = 0
        var currentPos = from
        var currentTurn = turn

        repeat(stepsLeft) {
            val neighbors = currentPos.getNeighbors().filter { grid.isValidPosition(it) }

            val bestNeighbor = neighbors.maxByOrNull {
                grid.getCell(it).estimateValueAt(currentTurn, currentTurn + 1)
            } ?: return@repeat

            totalScore += grid.getCell(bestNeighbor).estimateValueAt(currentTurn, currentTurn + 1)
            currentPos = bestNeighbor
            currentTurn += 1

            onEvaluate(SearchState.EvaluatingPotential(from, neighbors, bestNeighbor))
        }

        return totalScore
    }

}