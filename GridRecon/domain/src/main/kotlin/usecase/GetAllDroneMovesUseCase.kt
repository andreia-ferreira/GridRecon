package usecase

import entity.Drone
import repository.DroneRepositoryInterface

class GetAllDroneMovesUseCase(
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<GetAllDroneMovesUseCase.RequestParams, List<Drone.Move>> {
    override suspend fun execute(requestParams: RequestParams): List<Drone.Move> {
        return droneRepositoryInterface.getMoves(requestParams.droneId)
    }

    class RequestParams(val droneId: Long)
}