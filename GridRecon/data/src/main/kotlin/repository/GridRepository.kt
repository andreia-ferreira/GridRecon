package repository

import datasource.GridDataSource
import entity.Grid
import entity.Position
import net.penguin.domain.entity.GridType

class GridRepository(
    private val gridDataSource: GridDataSource
): GridRepositoryInterface {
    private lateinit var grid: Grid

    override suspend fun initializeGrid(gridType: GridType, regenerationRate: Double) {
        gridDataSource.get(gridType = gridType, regenerationRate = regenerationRate)?.let {
            grid = it
        }
    }

    override fun getCurrentGrid(): Grid {
        return grid
    }

    override fun consumeCell(position: Position, turn: Int) {
        val cell = grid.getCell(position)
        grid.replaceCell(
            cell = cell.copy(currentValue = 0.0, turnLastVisited = turn),
            position = position
        )
    }

    override fun regenerateCell(positions: List<Position>, turn: Int) {
        positions.forEach {
            val cell = grid.getCell(it)
            if (cell.canRegenerate(turn)) {
                grid.replaceCell(
                    cell = cell.copy(
                        currentValue = cell.currentValue + cell.regenerationRate
                    ),
                    position = it
                )
            }
        }
    }
}