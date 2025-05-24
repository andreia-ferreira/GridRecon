package net.penguin.app

import net.penguin.app.ConsolePrintUtils.printGrid
import net.penguin.app.ConsolePrintUtils.printSideBySideGrids
import net.penguin.domain.algorithm.DroneMovementAlgorithmInterface
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation

class SimulationRunner(private val algorithmInterface: DroneMovementAlgorithmInterface) {
    fun execute(simulation: Simulation) {
        val initialGrid = simulation.grid.copy()
        val exploredPositions = mutableSetOf<Position>()

        algorithmInterface.run(simulation) { state ->
            when (state) {
                is SearchState.AddCandidate -> {
                    println("\n=== Add candidate for Move : ${state.move.turn} ===")
                    printGrid(
                        grid = simulation.grid,
                        redTarget = state.move.position,
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                SearchState.Begin -> {
                    println("\n=== Begin ===")
                    printGrid(
                        grid = simulation.grid,
                        redTarget = simulation.startPosition,
                    )
                }
                is SearchState.EvaluatingPotential -> {
                    println("\n=== Evaluating potential ===")
                    println("Potential score for remaining steps: ${state.estimatedScore}")
                    exploredPositions.addAll(state.evaluatedPositions)
                    exploredPositions.add(state.from)
                    printGrid(
                        grid = simulation.grid,
                        redTarget = state.from,
                        highlightedPositions = state.evaluatedPositions,
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                is SearchState.Move -> {
                    println("\n=== Moving ===")
                    println("Turn score: ${simulation.drone.getAllMovesData().last().score} | " +
                            "Cumulative score: ${simulation.drone.getCumulativeScore()} ")
                    printGrid(
                        grid = simulation.grid,
                        redTarget = simulation.drone.getCurrentPosition(),
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                is SearchState.Result -> {
                    println("\n=== Final Result ===")
                    printSideBySideGrids(
                        initialGrid = initialGrid,
                        finalGrid = simulation.grid,
                        redInitial = simulation.drone.getPath().first(),
                        redFinal = simulation.drone.getPath().last(),
                        highlightedPositions = simulation.drone.getPath(),
                        explored = exploredPositions.toList()
                    )
                    println("Total Score: ${simulation.drone.getCumulativeScore()}")
                    println("Total Movements: ${simulation.drone.getPath().size - 1}")
                    println("Final path:\n${
                        simulation.drone.getAllMovesData().joinToString("\n") {
                            "Turn ${it.turn} : (${it.position.x}, ${it.position.y}) | Score: ${it.score}"
                        }
                    }")
                }
            }
        }
    }
}