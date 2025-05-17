package net.penguin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.penguin.domain.*

object GridFileReader: GridReaderInterface, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private const val GRID_FOLDER = "/grids"

    override fun get(initialParameters: InitialParameters): Deferred<Grid?> {
        return async {
            val content = object {}.javaClass.getResource("$GRID_FOLDER/20.txt")?.readText()
            val drone = initialParameters.drone
            val lines = content?.lines()?.filter { it.isNotBlank() }

            val rows: List<Row>? = lines?.mapIndexedNotNull { y, line ->
                line.trim()
                    .takeIf { it.isNotBlank() }
                    ?.split(" ")
                    ?.mapIndexed { x, cell ->
                        if (drone.matchesPosition(Position(x = x, y = lines.lastIndex - y))) {
                            Cell(0)
                        } else {
                            Cell(cell.toInt())
                        }
                    }
                    ?.let {
                        Row(it)
                    }
            }

            rows?.let { Grid(rows, drone) }
        }
    }
}