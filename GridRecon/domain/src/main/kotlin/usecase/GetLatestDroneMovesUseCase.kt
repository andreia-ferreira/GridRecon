package usecase

import entity.Drone
import repository.DroneRepositoryInterface

class GetLatestDroneMovesUseCase(
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<GetLatestDroneMovesUseCase.RequestParams, Drone.Move> {
    override suspend fun execute(requestParams: RequestParams): Drone.Move {
        return droneRepositoryInterface.getMoves(requestParams.droneId).last()
    }

    class RequestParams(val droneId: Long)
}