package usecase

import entity.Grid
import repository.GridRepositoryInterface

class GetCurrentGridUseCase(
    private val gridRepositoryInterface: GridRepositoryInterface
): UseCase.NoParamsUseCase<Grid> {
    override suspend fun execute(): Grid {
        return gridRepositoryInterface.getCurrentGrid()
    }
}