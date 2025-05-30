package usecase

import entity.Drone
import entity.SimulationParameters
import repository.DroneRepositoryInterface
import repository.GridRepositoryInterface

class InitializeSimulationUseCase(
    private val gridRepositoryInterface: GridRepositoryInterface,
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<InitializeSimulationUseCase.RequestParams, Unit> {
    override suspend fun execute(requestParams: RequestParams) {
        gridRepositoryInterface.initializeGrid(
            gridType = requestParams.simulationParameters.gridType,
            regenerationRate = requestParams.simulationParameters.cellRegenerationRate,
        )

        requestParams.simulationParameters.dronePositions.forEachIndexed { index, position ->
            droneRepositoryInterface.add(Drone(index.toLong()), position)
            gridRepositoryInterface.consumeCell(position = position, turn = 0)
        }
    }

    class RequestParams(val simulationParameters: SimulationParameters)
}