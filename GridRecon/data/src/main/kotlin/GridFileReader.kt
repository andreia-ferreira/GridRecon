package net.penguin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.penguin.domain.Grid
import net.penguin.domain.GridReaderInterface

object GridFileReader: GridReaderInterface, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private const val GRID_FOLDER = "/grids"

    override fun get(): Deferred<Grid?> {
        return async {
            val content = object {}.javaClass.getResource("$GRID_FOLDER/20.txt")?.readText()

            val rows: List<List<Int>> = content?.lines()?.mapNotNull { line ->
                line.trim()
                    .takeIf { it.isNotBlank() }
                    ?.split(" ")
                    ?.map { it.toInt() }
                    ?.toList()
            }.orEmpty()

            Grid(rows)
        }
    }
}