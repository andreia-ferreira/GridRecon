package net.penguin.domain.entity

data class DronePosition(
    val position: Position,
    val currentTurn: Int,
    val score: Int,
    val cumulativeScore: Int,
    val path: List<Position>
)