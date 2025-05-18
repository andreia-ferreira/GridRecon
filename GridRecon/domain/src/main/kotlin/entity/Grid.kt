package net.penguin.domain.entity

data class Grid(
    val rows: List<List<Cell>>,
    val regenerationRate: Int = 0
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun regenerateCells() {
        rows.flatten().forEach { it.regenerate(regenerationRate) }
    }

    fun isValidPosition(position: Position): Boolean {
        return position.x in 0 until size && position.y in 0 until size
    }
}