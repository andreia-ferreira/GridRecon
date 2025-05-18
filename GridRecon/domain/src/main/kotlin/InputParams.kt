package net.penguin.domain

data class InputParams(
    val gridSize: Int,
    val maxSteps: Int,
    val maxDuration: Long,
    val dronePosition: Position,
    val cellRegenerationRate: Int
)