package entity

data class Grid(
    val rows: List<List<Cell>>
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun deepCopy(): Grid {
        val copiedRows = rows.map { row ->
            row.map { it.copy() }.toMutableList()
        }
        return Grid(copiedRows)
    }
}