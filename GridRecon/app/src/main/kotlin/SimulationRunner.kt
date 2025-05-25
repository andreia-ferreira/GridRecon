package net.penguin.app

import algorithm.CandidateNextMove
import algorithm.DroneMovementAlgorithmInterface
import algorithm.SearchState
import entity.Drone
import entity.Grid
import entity.InputParams
import entity.Position
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
    private var isPrintToConsoleEnabled = false

    suspend fun execute(inputParams: InputParams) {
        initializeSimulationUseCase.execute(
            InitializeSimulationUseCase.RequestParams(
                inputParams = inputParams,
                regenerationRate = 0.5
            )
        )
        drones = getAvailableDronesUseCase.execute()

        if (inputParams.printToConsole) {
            initialGrid = getCurrentGridUseCase.execute().deepCopy()
            isPrintToConsoleEnabled = true
        }

        val startTime = System.currentTimeMillis()
        var currentTurn = 0

        displayState(SearchState.Begin)

        while (
            !isOutOfTime(startTime = startTime, maxTime = inputParams.maxDuration) &&
            !isOutOfMoves(currentTurn = currentTurn, maxTurns = inputParams.maxTurns)
        ) {
            val candidates = getCandidatesNextMove(inputParams)
            if (candidates.isEmpty() || isOutOfTime(startTime = startTime, maxTime = inputParams.maxDuration)) break

            val nextMove = pickBestMove(candidates)
            if (nextMove == null || isOutOfTime(startTime = startTime, maxTime = inputParams.maxDuration)) break
            executeMove(nextMove)

            currentTurn = nextMove.turn
        }

        elapsedTime = System.currentTimeMillis() - startTime
        displayState(SearchState.Finish)
    }

    private suspend fun getCandidatesNextMove(inputParams: InputParams): List<CandidateNextMove> {
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(drones.first().id))
        val grid = getCurrentGridUseCase.execute()
        val candidates = algorithmInterface.getCandidates(
            latestMove = droneMoves.last(),
            grid = grid,
            inputParams = inputParams,
        )
        exploredPositions.addAll(candidates.map { it.second.evaluatedPositions }.flatten())
        exploredPositions.addAll(candidates.map { it.second.from })
        displayState(SearchState.PotentialCandidates(candidates))
        return candidates
    }

    private suspend fun pickBestMove(candidates: List<CandidateNextMove>): Drone.Move? {
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        return algorithmInterface.getNextBestMove(droneMoves.last(), candidates)
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
        if (!isPrintToConsoleEnabled) return
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