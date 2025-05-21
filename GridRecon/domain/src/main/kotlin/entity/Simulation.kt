package net.penguin.domain.entity

class Simulation(
    val moves: Int,
    val maxDuration: Long,
    val grid: Grid,
    val startPosition: Position
)