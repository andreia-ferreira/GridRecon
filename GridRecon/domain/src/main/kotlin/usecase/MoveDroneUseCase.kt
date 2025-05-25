package usecase

import entity.Drone
import net.penguin.domain.usecase.UseCase
import repository.DroneRepositoryInterface
import repository.GridRepositoryInterface
import usecase.MoveDroneUseCase.RequestParams
import utils.getPath

class MoveDroneUseCase(
    private val gridRepositoryInterface: GridRepositoryInterface,
    private val droneRepositoryInterface: DroneRepositoryInterface
): UseCase.ParamsUseCase<RequestParams, Unit> {
    override suspend fun execute(requestParams: RequestParams) {
        val currentPath = droneRepositoryInterface.getMoves(requestParams.droneId).getPath()
        val currentTurn = requestParams.droneMove.turn
        val currentDronePosition = requestParams.droneMove.position

        gridRepositoryInterface.regenerateCell(currentPath, currentTurn)

        droneRepositoryInterface.move(droneId = requestParams.droneId, move = requestParams.droneMove)
        gridRepositoryInterface.consumeCell(position = currentDronePosition, turn = currentTurn)
    }

    class RequestParams(
        val droneId: Long,
        val droneMove: Drone.Move
    )
}