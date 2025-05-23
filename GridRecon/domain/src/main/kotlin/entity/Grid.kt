package net.penguin.domain.entity

data class Grid(
    val rows: List<List<Cell>>,
    val regenerationRate: Double = 0.0
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun regenerateCells(turn: Int) {
        if (turn > 0) {
            rows.flatten().forEach { it.regenerate(regenerationRate, turn) }
        }
    }

    fun isValidPosition(position: Position): Boolean {
        return position.x in 0 until size && position.y in 0 until size
    }

    fun estimateValueAt(
        position: Position,
        turn: Int,
    ): Int {
        val cell = getCell(position)
        val projectedValue = cell.getValue() + (regenerationRate * turn)
        return minOf(cell.initialValue, projectedValue.toInt())
    }
}