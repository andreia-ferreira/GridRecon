package net.penguin.domain.entity

data class Grid(
    val rows: List<List<Cell>>,
    val regenerationRate: Int = 0
) {
    fun allCells() = rows.flatten()

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun regenerateCells() {
        allCells().forEach { it.regenerate(regenerationRate) }
    }

    private fun isValidPosition(position: Position): Boolean {
        return try {
            getCell(position)
            true
        } catch (_: IndexOutOfBoundsException) {
            false
        }
    }
}