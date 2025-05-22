package net.penguin.domain.algorithm

import net.penguin.domain.entity.Simulation

interface DroneMovementAlgorithmInterface {
    fun run(simulation: Simulation, onStep: (SearchState) -> Unit): SearchState.Result
}