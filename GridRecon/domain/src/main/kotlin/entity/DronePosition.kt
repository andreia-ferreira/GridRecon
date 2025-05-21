package net.penguin.domain.entity

data class DronePosition(
    val position: Position,
    val timeStep: Int,
    val score: Int,
    val cumulativeScore: Int,
    val path: List<Position>,
)