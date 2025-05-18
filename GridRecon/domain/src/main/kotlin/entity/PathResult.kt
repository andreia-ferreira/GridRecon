package net.penguin.domain.entity

data class PathResult(
    val path: List<TimedPosition>,
    val totalScore: Int,
)
