package repository

import datasource.GridDataSource
import entity.Cell
import entity.Grid
import entity.GridType
import entity.Position

private typealias MutableGrid = List<MutableList<Cell>>

class GridRepository(
    private val gridDataSource: GridDataSource
): GridRepositoryInterface {
    private var mutableGrid = emptyList<MutableList<Cell>>()

    override suspend fun initializeGrid(gridType: GridType, regenerationRate: Double) {
        val fileContents = gridDataSource.getFileContents(gridType = gridType)
        getMutableGrid(fileContents, regenerationRate)?.let {
            mutableGrid = it
        }
    }

    private fun getMutableGrid(fileContent: List<String>?, cellRegenerationRate: Double): MutableGrid? {
        val rows: List<MutableList<Cell>>? = fileContent?.mapIndexedNotNull { y, line ->
            line.trim()
                .takeIf { it.isNotBlank() }
                ?.split(" ")
                ?.mapIndexedNotNull { x, cell ->
                    cell.toIntOrNull()?.let {
                        Cell(
                            maxValue = it,
                            currentValue = it.toDouble(),
                            regenerationRate = cellRegenerationRate
                        )
                    }
                }?.toMutableList()
        }.takeIf { it?.isEmpty() == false }

        return rows
    }

    override fun getCurrentGrid(): Grid {
        return Grid(mutableGrid)
    }

    override fun consumeCell(position: Position, turn: Int) {
        val currentCell = mutableGrid[mutableGrid.lastIndex - position.y][position.x]
        mutableGrid[mutableGrid.lastIndex - position.y][position.x] = currentCell.copy(
            currentValue = 0.0, turnLastVisited = turn
        )
    }

    override fun regenerateCell(positions: List<Position>, turn: Int) {
        positions.forEach {
            val cell = mutableGrid[mutableGrid.lastIndex - it.y][it.x]
            if (cell.canRegenerate(turn)) {
                mutableGrid[mutableGrid.lastIndex - it.y][it.x] = cell.copy(
                    currentValue = cell.currentValue + cell.regenerationRate
                )
            }
        }
    }
}