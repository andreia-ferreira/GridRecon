package entity

import net.penguin.domain.entity.GridType

data class SimulationParameters(
    val gridType: GridType,
    val maxTurns: Int,
    val maxDuration: Long,
    val dronePosition: Position,
    val cellRegenerationRate: Double,
    val printToConsole: Boolean
)