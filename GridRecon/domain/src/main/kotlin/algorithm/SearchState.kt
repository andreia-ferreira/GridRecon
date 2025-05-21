package net.penguin.domain.algorithm

import net.penguin.domain.entity.DronePosition
import net.penguin.domain.entity.Position

sealed class SearchState {
    data object Begin: SearchState()
    data class AddCandidate(val dronePosition: DronePosition): SearchState()
    data class EvaluatingPotential(val from: Position, val neighbors: List<Position>, val best: Position): SearchState()
    data class AddToBestOption(val dronePosition: DronePosition): SearchState()
    data class Move(val dronePosition: DronePosition): SearchState()
    data class Result(val path: List<Position>, val totalScore: Int): SearchState()
}