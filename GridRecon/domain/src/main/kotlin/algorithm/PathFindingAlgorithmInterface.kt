package net.penguin.domain.algorithm

import net.penguin.domain.entity.Simulation

interface PathFindingAlgorithmInterface {
    fun run(simulation: Simulation, onStep: (SearchState) -> Unit): SearchState.Result
}