package utils

import entity.Cell
import entity.Grid
import org.jetbrains.annotations.VisibleForTesting

object GridGenerator {
    @VisibleForTesting
    fun generate(matrix: List<MutableList<Int>>, regenerationRate: Double): Grid {
        return Grid(matrix.map { row -> row.map {
            Cell(maxValue = it, currentValue = it.toDouble(), regenerationRate = regenerationRate)
        }.toMutableList()})
    }
}