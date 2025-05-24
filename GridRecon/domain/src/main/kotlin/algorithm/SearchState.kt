package net.penguin.domain.algorithm

import net.penguin.domain.entity.Drone
import net.penguin.domain.entity.Position

sealed class SearchState {
    data object Begin: SearchState()
    data class AddCandidate(val move: Drone.Move): SearchState()
    data class EvaluatingPotential(val from: Position, val neighbors: List<Position>, val best: Position): SearchState()
    data class AddToBestOption(val move: Drone.Move): SearchState()
    data object Move: SearchState()
    data object Result: SearchState()
}