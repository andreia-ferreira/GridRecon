package net.penguin.app

import net.penguin.domain.algorithm.DroneMovementAlgorithmInterface
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation

class SimulationRunner(private val algorithmInterface: DroneMovementAlgorithmInterface) {
    fun execute(simulation: Simulation) {
        val exploredPositions = mutableSetOf<Position>()

        algorithmInterface.run(simulation) { state ->
            when (state) {
                is SearchState.AddCandidate -> {
                    println("\n=== Add candidate for Move : ${simulation.currentTurn + 1} ===")
                    simulation.grid.print(
                        redTarget = simulation.drone.getCurrentPosition(),
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                is SearchState.AddToBestOption -> {
                    println("\n=== Adding best option with Score ${state.move.score} for Move ${state.move.turn} ===")
                    simulation.grid.print(
                        greenTarget = state.move.position,
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                SearchState.Begin -> {
                    println("\n=== Begin ===")
                    simulation.grid.print(
                        redTarget = simulation.startPosition,
                    )
                }
                is SearchState.EvaluatingPotential -> {
                    println("\n=== Evaluating potential ===")
                    exploredPositions.addAll(state.neighbors)
                    exploredPositions.add(state.from)
                    simulation.grid.print(
                        redTarget = state.from,
                        greenTarget = state.best,
                        highlightedPositions = state.neighbors,
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                is SearchState.Move -> {
                    println("\n=== Moving with current score ${simulation.drone.getCumulativeScore()} ===")
                    simulation.grid.print(
                        redTarget = simulation.drone.getCurrentPosition(),
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
                    )
                }
                is SearchState.Result -> {
                    println("\n=== Final Result ===")
                    simulation.grid.print(
                        redTarget = simulation.drone.getCurrentPosition(),
                        highlightedPositions = simulation.drone.getPath(),
                        greyedOutPositions = exploredPositions.toList()
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