package repository

import entity.Grid
import entity.GridType
import entity.Position

interface GridRepositoryInterface {
    suspend fun initializeGrid(gridType: GridType, regenerationRate: Double)
    fun getCurrentGrid(): Grid
    fun consumeCell(position: Position, turn: Int)
    fun regenerateCell(positions: List<Position>, turn: Int)
}