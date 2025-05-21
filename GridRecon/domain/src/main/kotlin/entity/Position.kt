package net.penguin.domain.entity

data class Position(val x: Int, val y: Int) {
    fun getNeighbors(): List<Position> {
        return Direction.entries.map {
            Position(x + it.dirX, y + it.dirY)
        }
    }
}