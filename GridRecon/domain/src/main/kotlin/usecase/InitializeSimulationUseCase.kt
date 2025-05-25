package usecase

import entity.Drone
import entity.InputParams
import net.penguin.domain.usecase.UseCase
import repository.DroneRepositoryInterface
import repository.GridRepositoryInterface

class InitializeSimulationUseCase(
    private val gridRepositoryInterface: GridRepositoryInterface,
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<InitializeSimulationUseCase.RequestParams, Unit> {
    override suspend fun execute(requestParams: RequestParams) {
        gridRepositoryInterface.initializeGrid(
            gridType = requestParams.inputParams.gridType,
            regenerationRate = requestParams.regenerationRate,
        )

        droneRepositoryInterface.add(Drone(), requestParams.inputParams.dronePosition)
        gridRepositoryInterface.consumeCell(position = requestParams.inputParams.dronePosition, turn = 0)
    }

    class RequestParams(val inputParams: InputParams, val regenerationRate: Double)
}