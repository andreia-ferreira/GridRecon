package net.penguin.domain.algorithm

import kotlinx.coroutines.flow.Flow
import net.penguin.domain.entity.Simulation

interface PathFindingAlgorithmInterface {
    fun run(simulation: Simulation): Flow<SearchState>
}