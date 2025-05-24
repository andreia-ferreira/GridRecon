package net.penguin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.penguin.domain.GridReaderInterface
import net.penguin.domain.entity.Cell
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.GridType
import java.io.File

object GridFileReader: GridReaderInterface, CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private const val GRID_FOLDER = "../grids"

    override fun get(gridType: GridType, regenerationRate: Double): Deferred<Grid?> {
        return async {
            val content = getFileContent(gridType)
            val lines = content?.lines()?.filter { it.isNotBlank() }

            val rows: List<List<Cell>>? = lines?.mapNotNull { line ->
                line.trim()
                    .takeIf { it.isNotBlank() }
                    ?.split(" ")
                    ?.mapNotNull { cell ->
                        cell.toIntOrNull()?.let { Cell(initialValue = it, regenerationRate = regenerationRate) }
                    }
            }.takeIf { !it.isNullOrEmpty() }

            rows?.let { Grid(rows) }
        }
    }

    private fun getFileContent(gridType: GridType): String? {
        val gridsDir = File(GRID_FOLDER)
        if (!gridsDir.exists()) {
            println("The 'grids' folder does not exist: ${gridsDir.absolutePath}")
        }
        val fileName = "${gridType.size}.txt"

        val content = gridsDir.listFiles { file ->
            file.isFile && file.extension == "txt"
        }?.firstOrNull {
            it.name == fileName
        }?.readText()

        when {
            content == null -> println("$fileName not found.")
            content.isBlank() -> println("$fileName is empty")
        }

        return content
    }
}