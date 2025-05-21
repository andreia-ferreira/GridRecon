package net.penguin.app

import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position

fun Grid.print(
    redTarget: Position? = null,
    greenTarget: Position? = null,
    highlightedPositions: List<Position>? = emptyList()
) {
    val green = "\u001B[32m"
    val red = "\u001B[31m"
    val yellow = "\u001B[33m"
    val reset = "\u001B[0m"

    println(rows.mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            val currentCoordinates = Position(x = x, y = rows.lastIndex - y)
            when (currentCoordinates) {
                redTarget -> "${red}$cell${reset}"
                greenTarget -> "${green}$cell${reset}"
                highlightedPositions?.find { it == currentCoordinates } -> "${yellow}$cell${reset}"
                else -> cell.toString()
            }
        }.joinToString(" ")
    }.joinToString("\n"))
}