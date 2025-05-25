package utils

import entity.InputParams
import entity.Position
import net.penguin.domain.entity.GridType
import org.jetbrains.annotations.VisibleForTesting

object InputParamsGenerator {
    @VisibleForTesting
    fun generate(
        maxTurns: Int = 2,
        maxDuration: Long = 100,
        gridType: GridType = GridType.MEDIUM,
        cellRegenerationRate: Double = 0.0,
        dronePosition: Position,
        printToConsole: Boolean = false
    ): InputParams {
        return InputParams(
            maxTurns = maxTurns,
            maxDuration = maxDuration,
            gridType = gridType,
            dronePosition = dronePosition,
            cellRegenerationRate = cellRegenerationRate,
            printToConsole = printToConsole
        )
    }
}