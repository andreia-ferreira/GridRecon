package usecase

import entity.Drone
import net.penguin.domain.usecase.UseCase
import repository.DroneRepositoryInterface

class GetDroneMovesUseCase(
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<GetDroneMovesUseCase.RequestParams, List<Drone.Move>> {
    override suspend fun execute(requestParams: RequestParams): List<Drone.Move> {
        return droneRepositoryInterface.getMoves(requestParams.droneId)
    }

    class RequestParams(val droneId: Long)
}