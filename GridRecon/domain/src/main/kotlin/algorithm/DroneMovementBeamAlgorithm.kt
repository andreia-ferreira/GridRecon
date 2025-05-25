package algorithm

import entity.*

object DroneMovementBeamAlgorithm: DroneMovementAlgorithmInterface {
    private const val BEAM_WIDTH = 10

    override fun getCandidates(
        latestMove: Drone.Move,
        grid: Grid,
        inputParams: InputParams
    ): List<CandidateNextMove> {
        val candidates = mutableListOf<CandidateNextMove>()
        val nextTurn = latestMove.turn + 1

        val neighbors = latestMove.position.getValidNeighbors(grid.size)

        for (neighbor in neighbors) {
            if (nextTurn > inputParams.maxTurns) continue


            val cellValue = grid.getCell(neighbor).getValue()
            val cumulativeScore = latestMove.cumulativeScore + cellValue
            val potentialScore = estimatePotential(
                grid = grid,
                from = neighbor,
                turn = nextTurn,
                stepsLeft = inputParams.maxTurns - nextTurn
            )

            val nextDronePosition = Drone.Move(
                position = neighbor,
                turn = nextTurn,
                score = cellValue,
                parent = latestMove,
                cumulativeScore = cumulativeScore
            )

            candidates.add(nextDronePosition to potentialScore)
        }
        return candidates
    }

    override fun getNextBestMove(
        latestMove: Drone.Move,
        candidates: List<CandidateNextMove>
    ): Drone.Move? {
        val sortedCandidates = candidates
            .sortedWith(compareByDescending<CandidateNextMove> { it.second.value + it.first.score }
                .thenByDescending { it.first.score })
            .take(BEAM_WIDTH)

        val bestNextMove = sortedCandidates
            .map { it.first }
            .firstOrNull {
                it.turn == latestMove.turn + 1 && it.position.isNeighbor(latestMove.position)
            }

        return bestNextMove
    }

    private fun estimatePotential(
        grid: Grid,
        from: Position,
        turn: Int,
        stepsLeft: Int
    ): EstimatedScore {
        var totalScore = 0
        var currentPos = from
        var currentTurn = turn
        val evaluatedPositions = mutableSetOf<Position>()

        repeat(stepsLeft) {
            val neighbors = currentPos.getValidNeighbors(grid.size)
            evaluatedPositions.addAll(neighbors)

            val bestNeighbor = neighbors.maxByOrNull {
                grid.getCell(it).estimateValueAt(currentTurn, currentTurn + 1)
            } ?: return@repeat

            totalScore += grid.getCell(bestNeighbor).estimateValueAt(currentTurn, currentTurn + 1)
            currentPos = bestNeighbor
            currentTurn += 1
        }

        return EstimatedScore(value = totalScore, from = from, evaluatedPositions = evaluatedPositions.toList())
    }
}