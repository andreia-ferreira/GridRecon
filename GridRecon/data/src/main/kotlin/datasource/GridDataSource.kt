package datasource

import entity.Cell
import entity.Grid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.penguin.domain.entity.GridType
import java.io.File

object GridDataSource {
    private const val GRID_FOLDER = "../grids"

    suspend fun get(gridType: GridType, regenerationRate: Double): Grid? {
        return withContext(Dispatchers.Default) {
            val content = getFileContent(gridType)
            val lines = content?.lines()?.filter { it.isNotBlank() }

            val rows: List<MutableList<Cell>>? = lines?.mapIndexedNotNull { y, line ->
                line.trim()
                    .takeIf { it.isNotBlank() }
                    ?.split(" ")
                    ?.mapIndexedNotNull { x, cell ->
                        cell.toIntOrNull()?.let {
                            Cell(
                                maxValue = it,
                                currentValue = it.toDouble(),
                                regenerationRate = regenerationRate
                            )
                        }
                    }?.toMutableList()
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