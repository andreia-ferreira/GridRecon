package entity

data class Grid(
    val rows: List<MutableList<Cell>>
) {
    val size = rows.size

    fun getCell(position: Position): Cell {
        return rows[rows.lastIndex - position.y][position.x]
    }

    fun replaceCell(cell: Cell, position: Position) {
        rows[rows.lastIndex - position.y][position.x] = cell
    }

    fun deepCopy(): Grid {
        val copiedRows = rows.map { row ->
            row.map { it.copy() }.toMutableList()
        }
        return Grid(copiedRows)
    }
}