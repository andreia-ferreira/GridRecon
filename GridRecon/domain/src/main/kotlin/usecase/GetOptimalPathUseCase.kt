//package net.penguin.domain.usecase
//
//import net.penguin.domain.entity.*
//import kotlin.math.abs
//import kotlin.math.max
//
//class GetOptimalPathUseCase: UseCase.ParamsUseCase<GetOptimalPathUseCase.RequestParams, PathResult> {
//    override suspend fun execute(requestParams: RequestParams): PathResult {
//        return findPathWithBeamSearch(requestParams.simulation)
//    }
//
//    private fun findPathWithBeamSearch(
//        simulation: Simulation,
//        beamWidth: Int = 10
//    ): PathResult {
//        val grid = simulation.grid
//        val start = simulation.drone.currentPosition
//        val maxSteps = simulation.maxSteps
//        val timeLimitMillis = simulation.maxDuration
//
//        val startTime = System.currentTimeMillis()
//
//        val startCell = grid.getCell(start)
//        val startScore = startCell.valueAtTimeStep(0, grid.regenerationRate)
//
//        var currentBeam = listOf(TimedPosition(start, 0, startScore, startScore))
//        var pathMap = mutableMapOf(currentBeam[0] to listOf(currentBeam[0]))
//        var bestPath = pathMap[currentBeam[0]]!!
//        var bestScore = startScore
//
//        for (step in 1 until maxSteps) {
//            if (System.currentTimeMillis() - startTime > timeLimitMillis) break
//
//            val candidates = mutableListOf<Pair<TimedPosition, List<TimedPosition>>>()
//
//            for (current in currentBeam) {
//                val currentPath = pathMap[current] ?: continue
//
//                for ((dx, dy) in Direction.entries.map { it.vector }) {
//                    val newPos = Position(current.position.x + dx, current.position.y + dy)
//                    if (!grid.isValidPosition(newPos)) continue
//
//                    val arrivalStep = current.timeStep + 1
//                    val cell = grid.getCell(newPos)
//                    val score = cell.valueAtTimeStep(arrivalStep, grid.regenerationRate)
//
//                    val cumulative = current.cumulativeScore + score
//                    val newTimed = TimedPosition(newPos, arrivalStep, score, cumulative)
//                    val newPath = currentPath + newTimed
//
//                    candidates += newTimed to newPath
//
//                    if (cumulative > bestScore) {
//                        bestScore = cumulative
//                        bestPath = newPath
//                    }
//                }
//            }
//
//            if (candidates.isEmpty()) break
//
//            val sorted = candidates.sortedByDescending {
//                it.first.cumulativeScore + estimatePotentialScore(
//                    grid,
//                    it.first.position,
//                    it.first.timeStep,
//                    maxSteps - it.first.timeStep
//                )
//            }
//
//            currentBeam = sorted.take(beamWidth).map { it.first }
//            pathMap = sorted.take(beamWidth).associate { it.first to it.second }.toMutableMap()
//        }
//
//        return PathResult(bestPath, bestScore)
//    }
//
//    private fun estimatePotentialScore(
//        grid: Grid,
//        start: Position,
//        currentStep: Int,
//        remainingSteps: Int
//    ): Int {
//        val scores = mutableListOf<Pair<Position, Int>>()
//        val size = grid.rows.size
//
//        for (y in 0 until size) {
//            for (x in 0 until size) {
//                val pos = Position(x, y)
//                val dist = max(abs(x - start.x), abs(y - start.y))
//                if (dist <= remainingSteps) {
//                    val arrivalStep = currentStep + dist
//                    val value = grid.getCell(pos).valueAtTimeStep(arrivalStep, grid.regenerationRate)
//                    scores.add(pos to value)
//                }
//            }
//        }
//
//        scores.sortByDescending { it.second }
//
//        var estimate = 0
//        var stepsLeft = remainingSteps
//        var current = start
//
//        for ((target, value) in scores) {
//            val dist = max(abs(target.x - current.x), abs(target.y - current.y))
//            if (dist <= stepsLeft) {
//                estimate += value
//                stepsLeft -= dist
//                current = target
//            }
//            if (stepsLeft <= 0) break
//        }
//
//        return estimate
//    }
//
//    class RequestParams(val simulation: Simulation)
//}
//
