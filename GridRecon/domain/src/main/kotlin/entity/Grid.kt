package net.penguin.domain.entity

data class Grid(
    val rows: List<List<Cell>>,
    val regenerationRate: Double = 0.0
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun regenerateCells(timeStep: Int) {
        rows.flatten().forEach { it.regenerate(regenerationRate, timeStep) }
    }

    fun isValidPosition(position: Position): Boolean {
        return position.x in 0 until size && position.y in 0 until size
    }

    fun estimateValueAt(
        position: Position,
        timeStep: Int,
    ): Int {
        val cell = getCell(position)
        val projectedValue = cell.getValue() + (regenerationRate * timeStep)
        return minOf(cell.initialValue, projectedValue.toInt())
    }

    fun clone(): Grid {
        val newRows = rows.map { row -> row.map { it.copyForClone() } }
        return Grid(newRows, regenerationRate)
    }
}