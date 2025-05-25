package datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.penguin.domain.entity.GridType
import java.io.File

object GridDataSource {
    private const val GRID_FOLDER = "../grids"

    suspend fun getFileContents(gridType: GridType): List<String>? {
        return withContext(Dispatchers.Default) {
            val content = getFileContent(gridType)
            val lines = content?.lines()?.filter { it.isNotBlank() }

            lines
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