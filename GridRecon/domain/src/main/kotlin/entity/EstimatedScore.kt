package entity

typealias CandidateNextMove = Pair<Drone.Move, EstimatedScore>
data class EstimatedScore(val value: Int, val from: Position, val evaluatedPositions: List<Position>)