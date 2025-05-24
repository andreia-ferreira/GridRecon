package net.penguin.domain.entity

class Grid(
    val rows: List<List<Cell>>
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun regenerateAll(turn: Int) {
        if (turn > 0) {
            rows.flatten().forEach { it.regenerate(turn) }
        }
    }

    fun isValidPosition(position: Position): Boolean {
        return position.x in 0 until size && position.y in 0 until size
    }

    fun copy(): Grid {
        val copiedRows = rows.map { row ->
            row.map { cell -> cell.copy() }
        }
        return Grid(copiedRows)
    }
}