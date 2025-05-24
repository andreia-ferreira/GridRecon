package net.penguin.domain.usecase

import net.penguin.domain.GridReaderInterface
import net.penguin.domain.entity.InputParams
import net.penguin.domain.entity.Simulation

class GetSimulationUseCase(
    private val gridReaderInterface: GridReaderInterface
): UseCase.ParamsUseCase<GetSimulationUseCase.RequestParams, Simulation?> {
    override suspend fun execute(requestParams: RequestParams): Simulation? {
        val grid = gridReaderInterface.get(
            gridType = requestParams.inputParams.gridType,
            regenerationRate = requestParams.regenerationRate
        ).await() ?: return null

        return Simulation(
            maxMoves = requestParams.inputParams.maxTurns,
            maxDuration = requestParams.inputParams.maxDuration,
            grid = grid,
            startPosition = requestParams.inputParams.dronePosition
        ).also {
            grid.getCell(requestParams.inputParams.dronePosition).consume(0)
        }
    }

    class RequestParams(val inputParams: InputParams, val regenerationRate: Double)
}