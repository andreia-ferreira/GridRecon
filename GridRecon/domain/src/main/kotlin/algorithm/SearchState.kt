package algorithm

import entity.Drone

sealed class SearchState {
    data object Begin: SearchState()
    data class PotentialCandidates(val candidates: List<CandidateNextMove>): SearchState()
    data class Move(val move: Drone.Move?): SearchState()
    data object Finish: SearchState()
}