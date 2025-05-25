package entity

data class SimulationParameters(
    val gridType: GridType,
    val maxTurns: Int,
    val maxDuration: Long,
    val dronePositions: List<Position>,
    val cellRegenerationRate: Double,
    val printIntermediateSteps: Boolean
)