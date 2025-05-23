package net.penguin.domain.entity

data class InputParams(
    val gridType: GridType,
    val maxSteps: Int,
    val maxDuration: Long,
    val dronePosition: Position,
    val cellRegenerationRate: Double
)