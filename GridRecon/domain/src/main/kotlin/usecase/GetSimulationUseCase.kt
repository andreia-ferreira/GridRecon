package net.penguin.domain.usecase

import net.penguin.domain.Drone
import net.penguin.domain.GridReaderInterface
import net.penguin.domain.InputParams
import net.penguin.domain.Simulation

class GetSimulationUseCase(
    private val gridReaderInterface: GridReaderInterface
): UseCase.ParamsUseCase<GetSimulationUseCase.RequestParams, Simulation?> {
    override suspend fun execute(requestParams: RequestParams): Simulation? {
        val originalGrid = gridReaderInterface.get().await() ?: return null
        val drone = Drone(requestParams.inputParams.dronePosition)

        return Simulation(
            maxSteps = requestParams.inputParams.maxSteps,
            maxDuration = requestParams.inputParams.maxDuration,
            grid = originalGrid.copy(regenerationRate = requestParams.inputParams.cellRegenerationRate),
            drone = drone
        ).also {
            originalGrid.getCell(requestParams.inputParams.dronePosition).consume()
        }
    }

    class RequestParams(val inputParams: InputParams)
}