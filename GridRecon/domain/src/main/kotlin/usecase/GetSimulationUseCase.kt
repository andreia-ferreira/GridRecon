package net.penguin.domain.usecase

import net.penguin.domain.GridReaderInterface
import net.penguin.domain.entity.InputParams
import net.penguin.domain.entity.Simulation

class GetSimulationUseCase(
    private val gridReaderInterface: GridReaderInterface
): UseCase.ParamsUseCase<GetSimulationUseCase.RequestParams, Simulation?> {
    override suspend fun execute(requestParams: RequestParams): Simulation? {
        val originalGrid = gridReaderInterface.get(requestParams.inputParams.gridType).await() ?: return null

        return Simulation(
            maxMoves = requestParams.inputParams.maxTurns,
            maxDuration = requestParams.inputParams.maxDuration,
            grid = originalGrid.copy(regenerationRate = requestParams.inputParams.cellRegenerationRate),
            startPosition = requestParams.inputParams.dronePosition
        ).also {
            originalGrid.getCell(requestParams.inputParams.dronePosition).consume(0)
        }
    }

    class RequestParams(val inputParams: InputParams)
}