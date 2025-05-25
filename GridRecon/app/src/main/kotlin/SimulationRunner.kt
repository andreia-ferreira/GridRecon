package net.penguin.app

import algorithm.DroneMovementAlgorithmInterface
import algorithm.SearchState
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
    private val getDroneMovesUseCase: GetDroneMovesUseCase,
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

        val startTime = System.currentTimeMillis()
        var currentTurn = 0

        displayState(SearchState.Begin)

        while (
            !isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration) &&
            !isOutOfMoves(currentTurn = currentTurn, maxTurns = simulationParameters.maxTurns)
        ) {
            val candidates = getCandidatesNextMove(simulationParameters)
            if (candidates.isEmpty() || isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration)) break

            val nextMove = pickBestMove(candidates, simulationParameters)
            if (nextMove == null || isOutOfTime(startTime = startTime, maxTime = simulationParameters.maxDuration)) break
            executeMove(nextMove)

            currentTurn = nextMove.turn
        }

        elapsedTime = System.currentTimeMillis() - startTime
        displayState(SearchState.Finish)
    }

    private suspend fun getCandidatesNextMove(simulationParameters: SimulationParameters): List<CandidateNextMove> {
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(drones.first().id))
        val grid = getCurrentGridUseCase.execute()
        val candidates = algorithmInterface.getCandidates(
            latestMove = droneMoves.last(),
            grid = grid,
            simulationParameters = simulationParameters,
        )
        exploredPositions.addAll(candidates.map { it.second.evaluatedPositions }.flatten())
        exploredPositions.addAll(candidates.map { it.second.from })
        displayState(SearchState.PotentialCandidates(candidates))
        return candidates
    }

    private suspend fun pickBestMove(candidates: List<CandidateNextMove>, simulationParameters: SimulationParameters): Drone.Move? {
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        return algorithmInterface.getNextBestMove(droneMoves.last(), candidates, simulationParameters)
    }

    private suspend fun executeMove(move: Drone.Move) {
        moveDroneUseCase.execute(MoveDroneUseCase.RequestParams(
            droneId = drones.first().id,
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
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(drones.first().id))

        when (state) {
            SearchState.Begin -> {
                println("\n=== Begin ===")
                printGrid(
                    grid = grid,
                    redTarget = droneMoves.last().position,
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
                    redTarget = droneMoves.last().position,
                    greenTargets = state.candidates.map { it.second.from },
                    highlightedPositions = droneMoves.map { it.position },
                    greyedOutPositions = exploredPositions.toList()
                )
            }
            is SearchState.Move -> {
                println("\n=== Moving ===")
                println("Turn ${state.move?.turn ?: 0} score: ${state.move?.score ?: 0} | " +
                        "Cumulative score: ${state.move?.cumulativeScore ?: 0} ")
                printGrid(
                    grid = grid,
                    redTarget = droneMoves.last().position,
                    highlightedPositions = droneMoves.map { it.position },
                    greyedOutPositions = exploredPositions.toList()
                )
            }

            is SearchState.Finish -> {
                println("\n=== Final Result ===")
                printSideBySideGrids(
                    initialGrid = initialGrid,
                    finalGrid = grid,
                    redInitial = droneMoves.first().position,
                    redFinal = droneMoves.last().position,
                    highlightedPositions = droneMoves.map { it.position },
                    explored = exploredPositions.toList()
                )
                println("Total Score: ${droneMoves.last().cumulativeScore}")
                println("Total Movements: ${droneMoves.size - 1}")
                println("Elapsed Time: $elapsedTime")
                println("Final path:\n${
                    droneMoves.joinToString("\n") {
                        "Turn ${it.turn} : (${it.position.x}, ${it.position.y}) | Score: ${it.score}"
                    }
                }")
            }
        }
    }
}