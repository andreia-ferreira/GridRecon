package usecase

import entity.Drone
import repository.DroneRepositoryInterface

class GetAvailableDronesUseCase(
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.NoParamsUseCase<List<Drone>> {
    override suspend fun execute(): List<Drone> {
        return droneRepositoryInterface.getAllDrones()
    }
}