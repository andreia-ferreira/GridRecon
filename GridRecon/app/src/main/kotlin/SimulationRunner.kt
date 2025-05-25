package net.penguin.app

import algorithm.DroneMovementAlgorithmInterface
import entity.*
import net.penguin.app.ConsolePrintUtils.printGrid
import net.penguin.app.ConsolePrintUtils.printSideBySideGrids
import org.jetbrains.annotations.VisibleForTesting
import usecase.*

class SimulationRunner(
    private val initializeSimulationUseCase: InitializeSimulationUseCase,
    private val getAvailableDronesUseCase: GetAvailableDronesUseCase,
    private val getCurrentGridUseCase: GetCurrentGridUseCase,
    private val moveDroneUseCase: MoveDroneUseCase,
    private val getAllDroneMovesUseCase: GetAllDroneMovesUseCase,
    private val getLatestDroneMovesUseCase: GetLatestDroneMovesUseCase,
    private val algorithmInterface: DroneMovementAlgorithmInterface,
) {
    private var exploredPositions = mutableSetOf<Position>()
    private var drones = emptyList<Drone>()
    @VisibleForTesting var elapsedTime: Long = -1
    private var initialGrid: Grid? = null
    private var printIntermediateSteps = false

    suspend fun execute(simulationParameters: SimulationParameters) {
        initializeSimulationUseCase.execute(
            InitializeSimulationUseCase.RequestParams(simulationParameters = simulationParameters)
        )
        drones = getAvailableDronesUseCase.execute()

        if (simulationParameters.printIntermediateSteps) {
            initialGrid = getCurrentGridUseCase.execute().deepCopy()
            printIntermediateSteps = true
        }

        displayState(SearchState.Begin)
        val startTime = System.currentTimeMillis()
        var currentTurn = 0

        while (
            !isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration) &&
            !isOutOfMoves(currentTurn = currentTurn, maxTurns = simulationParameters.maxTurns)
        ) {
            for (drone in drones) {
                val latestDroneMove = getLatestDroneMovesUseCase.execute(GetLatestDroneMovesUseCase.RequestParams(drone.id))
                val candidates = getCandidatesNextMove(simulationParameters, latestDroneMove)
                if (candidates.isEmpty() || isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration)) break

                val nextMove = pickBestMove(candidates, simulationParameters, latestDroneMove)
                if (nextMove == null || isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration)) break
                executeMove(nextMove, drone)

                currentTurn = nextMove.turn
            }
        }

        elapsedTime = System.currentTimeMillis() - startTime
        displayState(SearchState.Finish)
    }

    private suspend fun getCandidatesNextMove(
        simulationParameters: SimulationParameters,
        latestDroneMove: Drone.Move
    ): List<CandidateNextMove> {
        val otherDronesPositions = drones.map {
            getLatestDroneMovesUseCase.execute(GetLatestDroneMovesUseCase.RequestParams(it.id)).position
        }
        val grid = getCurrentGridUseCase.execute()
        val candidates = algorithmInterface.getCandidates(
            latestMove = latestDroneMove,
            grid = grid,
            simulationParameters = simulationParameters,
            forbidPositions = otherDronesPositions
        )
        exploredPositions.addAll(candidates.map { it.second.evaluatedPositions }.flatten())
        exploredPositions.addAll(candidates.map { it.second.from })
        displayState(SearchState.PotentialCandidates(candidates))
        return candidates
    }

    private fun pickBestMove(
        candidates: List<CandidateNextMove>,
        simulationParameters: SimulationParameters,
        latestDroneMove: Drone.Move
    ): Drone.Move? {
        return algorithmInterface.getNextBestMove(latestDroneMove, candidates, simulationParameters)
    }

    private suspend fun executeMove(move: Drone.Move, drone: Drone) {
        moveDroneUseCase.execute(MoveDroneUseCase.RequestParams(
            droneId = drone.id,
            droneMove = move
        ))
        displayState(SearchState.Move(move))
    }

    private fun isOutOfTime(startTime: Long, maxTime: Long): Boolean {
        return System.currentTimeMillis() - startTime >= maxTime
    }

    private fun isOutOfMoves(currentTurn: Int, maxTurns: Int): Boolean {
        return currentTurn >= maxTurns
    }

    private suspend fun displayState(state: SearchState) {
        if (!printIntermediateSteps && state != SearchState.Finish) return
        val grid = getCurrentGridUseCase.execute()
        val dronesMoves = drones.associateWith { drone ->
            getAllDroneMovesUseCase.execute(GetAllDroneMovesUseCase.RequestParams(drone.id))
        }

        when (state) {
            SearchState.Begin -> {
                println("\n=== Begin ===")
                printGrid(
                    grid = grid,
                    redTargets = dronesMoves.values.mapNotNull { it.lastOrNull()?.position },
                )
            }

            is SearchState.PotentialCandidates -> {
                println("\n=== Evaluating potential candidates ===")
                println(state.candidates.joinToString("\n") {
                    "Candidate: (${it.second.from.x}, ${it.second.from.y}) | " +
                            "Potential score: ${it.second.value}"
                })
                printGrid(
                    grid = grid,
                    redTargets = dronesMoves.values.mapNotNull { it.lastOrNull()?.position },
                    greenTargets = state.candidates.map { it.second.from },
                    highlightedPositions = dronesMoves.values.flatten().map { it.position },
                    greyedOutPositions = exploredPositions.toList()
                )
            }

            is SearchState.Move -> {
                println("\n=== Moving ===")
                println(
                    "Turn ${state.move?.turn ?: 0} score: ${state.move?.score ?: 0} | " +
                            "Cumulative score: ${state.move?.cumulativeScore ?: 0} "
                )
                printGrid(
                    grid = grid,
                    redTargets = dronesMoves.values.mapNotNull { it.lastOrNull()?.position },
                    highlightedPositions = dronesMoves.values.flatten().map { it.position },
                    greyedOutPositions = exploredPositions.toList()
                )
            }

            is SearchState.Finish -> {
                println("\n=== Final Result ===")
                printSideBySideGrids(
                    initialGrid = initialGrid,
                    finalGrid = grid,
                    redInitials = dronesMoves.values.mapNotNull { it.firstOrNull()?.position },
                    redFinals = dronesMoves.values.mapNotNull { it.lastOrNull()?.position },
                    highlightedPositions = dronesMoves.values.flatten().map { it.position },
                    explored = exploredPositions.toList()
                )
                val totalScore = dronesMoves.values.sumOf { it.lastOrNull()?.cumulativeScore ?: 0 }
                val totalMovements = dronesMoves[drones.first()]?.lastIndex ?: 0
                println("Total Score: $totalScore")
                println("Total Movements: $totalMovements")
                println("Elapsed Time: $elapsedTime")
                drones.forEach { drone ->
                    val moves = dronesMoves[drone] ?: emptyList()
                    println("\nFinal path for drone ${drone.id}:")
                    moves.forEach {
                        println("Turn ${it.turn} : (${it.position.x}, ${it.position.y}) | Score: ${it.score}")
                    }
                }
            }
        }
    }
}