package entity

import kotlin.math.abs

data class Position(val x: Int, val y: Int) {

    fun getValidNeighbors(gridSize: Int): List<Position> {
        return Direction.entries.mapNotNull { d ->
            Position(x + d.dirX, y + d.dirY)
                .takeIf { it.x in 0 until gridSize && it.y in 0 until gridSize }
        }
    }

    fun isNeighbor(position: Position): Boolean {
        val dx = abs(this.x - position.x)
        val dy = abs(this.y - position.y)
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)
    }
}