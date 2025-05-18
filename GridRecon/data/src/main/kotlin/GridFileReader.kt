package net.penguin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.penguin.domain.GridReaderInterface
import net.penguin.domain.entity.Cell
import net.penguin.domain.entity.Grid

object GridFileReader: GridReaderInterface, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private const val GRID_FOLDER = "/grids"

    override fun get(): Deferred<Grid?> {
        return async {
            val content = object {}.javaClass.getResource("$GRID_FOLDER/20.txt")?.readText()
            val lines = content?.lines()?.filter { it.isNotBlank() }

            val rows: List<List<Cell>>? = lines?.mapNotNull { line ->
                line.trim()
                    .takeIf { it.isNotBlank() }
                    ?.split(" ")
                    ?.map { cell ->
                        Cell(cell.toInt())
                    }
            }.takeIf { !it.isNullOrEmpty() }

            rows?.let { Grid(rows) }
        }
    }
}