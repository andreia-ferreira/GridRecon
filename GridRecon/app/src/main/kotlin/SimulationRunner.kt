package net.penguin.app

import net.penguin.domain.algorithm.DroneMovementAlgorithmInterface
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Simulation

class SimulationRunner(private val algorithmInterface: DroneMovementAlgorithmInterface) {
    fun execute(simulation: Simulation) {
        algorithmInterface.run(simulation) { state ->
            when (state) {
                is SearchState.AddCandidate -> {
                    println("\n=== Add candidate for Move : ${state.dronePosition.currentTurn} ===")
                    simulation.grid.print(
                        redTarget = state.dronePosition.position,
                        highlightedPositions = state.dronePosition.path,
                    )
                }
                is SearchState.AddToBestOption -> {
                    println("\n=== Adding best option with Score ${state.dronePosition.score} for Move ${state.dronePosition.currentTurn} ===")
                    simulation.grid.print(
                        greenTarget = state.dronePosition.position,
                        highlightedPositions = state.dronePosition.path,
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
                    simulation.grid.print(
                        redTarget = state.from,
                        greenTarget = state.best,
                        highlightedPositions = state.neighbors
                    )
                }
                is SearchState.Move -> {
                    println("\n=== Moving with current score ${state.dronePosition.cumulativeScore} ===")
                    simulation.grid.print(
                        redTarget = state.dronePosition.position,
                        highlightedPositions = state.dronePosition.path
                    )
                }
                is SearchState.Result -> {
                    println("\n=== Final Result ===")
                    simulation.grid.print(
                        redTarget = state.path.last(),
                        highlightedPositions = state.path
                    )
                    println("Total Score: ${state.totalScore}")
                    println("Total Movements: ${state.path.size - 1}")
                    println("Final path:\n${state.path.mapIndexed { index, position ->  
                        "Turn $index : (${position.x}, ${position.y})"
                    }.joinToString("\n")}")
                }
            }
        }
    }
}