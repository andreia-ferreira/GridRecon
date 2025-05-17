package net.penguin.domain

data class InitialParameters(
    val gridSize: Int,
    val maxSteps: Int,
    val maxDuration: Long,
    val droneCoordinates: Pair<Int, Int>
)