package usecase

import entity.Drone
import net.penguin.domain.usecase.UseCase
import repository.DroneRepositoryInterface

class GetAvailableDronesUseCase(
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.NoParamsUseCase<List<Drone>> {
    override suspend fun execute(): List<Drone> {
        return droneRepositoryInterface.getAllDrones()
    }
}