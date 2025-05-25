package repository

import entity.Grid
import entity.Position
import net.penguin.domain.entity.GridType

interface GridRepositoryInterface {
    suspend fun initializeGrid(gridType: GridType, regenerationRate: Double)
    fun getCurrentGrid(): Grid
    fun consumeCell(position: Position, turn: Int)
    fun regenerateCell(positions: List<Position>, turn: Int)
}