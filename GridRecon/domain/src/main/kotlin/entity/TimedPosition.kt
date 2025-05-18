package net.penguin.domain.entity

data class TimedPosition(
    val position: Position,
    val timeStep: Int,
    val score: Int,
    val cumulativeScore: Int
)