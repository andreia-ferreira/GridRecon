package net.penguin.app

import net.penguin.domain.algorithm.PathFindingAlgorithmInterface
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Simulation

class SimulationRunner(private val algorithmInterface: PathFindingAlgorithmInterface) {
    fun execute(simulation: Simulation) {
        algorithmInterface.run(simulation) { state ->
            when (state) {
                is SearchState.AddCandidate -> {
                    println("\n=== Add candidate for Move : ${state.dronePosition.timeStep} ===")
                    simulation.grid.print(
                        redTarget = state.dronePosition.position,
                        highlightedPositions = state.dronePosition.path,
                    )
                }
                is SearchState.AddToBestOption -> {
                    println("\n=== Adding best option with Score ${state.dronePosition.score} for Move ${state.dronePosition.timeStep} ===")
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
                    simulation.grid.getCell(state.dronePosition.position).consume(state.dronePosition.timeStep)
                    simulation.grid.print(
                        redTarget = state.dronePosition.position,
                        highlightedPositions = state.dronePosition.path
                    )
                    simulation.grid.regenerateCells(state.dronePosition.timeStep)
                }
                is SearchState.Result -> {
                    println("\n=== Final Result ===")
                    simulation.grid.print(
                        redTarget = state.path.last(),
                        highlightedPositions = state.path
                    )
                    println("Total Score: ${state.totalScore}")
                    println("Time Steps Used: ${state.path.size - 1}")
                    println("Final path: ${state.path}")
                }
            }
        }
    }
}