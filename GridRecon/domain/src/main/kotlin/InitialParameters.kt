package net.penguin.domain

data class InitialParameters(
    val gridSize: Int,
    val maxSteps: Int,
    val maxDuration: Long,
    val drone: Drone,
    val cellRegenerationRate: Int
)