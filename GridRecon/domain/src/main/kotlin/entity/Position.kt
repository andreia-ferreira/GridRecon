package net.penguin.domain.entity

import kotlin.math.abs

data class Position(val x: Int, val y: Int) {

    fun getNeighbors(): List<Position> {
        return Direction.entries.map {
            Position(x + it.dirX, y + it.dirY)
        }
    }

    fun isNeighbor(position: Position): Boolean {
        val dx = abs(this.x - position.x)
        val dy = abs(this.y - position.y)
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)
    }
}