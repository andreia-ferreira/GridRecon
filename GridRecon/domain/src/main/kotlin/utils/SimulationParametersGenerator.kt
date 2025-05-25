package utils

import entity.GridType
import entity.Position
import entity.SimulationParameters
import org.jetbrains.annotations.VisibleForTesting

object SimulationParametersGenerator {
    @VisibleForTesting
    fun generate(
        maxTurns: Int = 2,
        maxDuration: Long = 100,
        gridType: GridType = GridType.MEDIUM,
        cellRegenerationRate: Double = 0.0,
        dronePositions: List<Position>,
        printToConsole: Boolean = false
    ): SimulationParameters {
        return SimulationParameters(
            maxTurns = maxTurns,
            maxDuration = maxDuration,
            gridType = gridType,
            dronePositions = dronePositions,
            cellRegenerationRate = cellRegenerationRate,
            printIntermediateSteps = printToConsole
        )
    }
}