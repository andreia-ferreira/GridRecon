package net.penguin.domain.entity

data class InputParams(
    val gridSize: Int,
    val maxSteps: Int,
    val maxDuration: Long,
    val dronePosition: Position,
    val cellRegenerationRate: Double
)