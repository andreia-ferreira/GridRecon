package net.penguin.app

import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position

fun Grid.print(
    redTarget: Position? = null,
    greenTarget: Position? = null,
    highlightedPositions: List<Position>? = null,
    greyedOutPositions: List<Position>? = null
) {
    val green = "\u001B[32m"
    val red = "\u001B[31m"
    val yellow = "\u001B[33m"
    val greyBackground = "\u001B[48;5;232m"
    val reset = "\u001B[0m"

    println(rows.mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            var cellText = ""
            val cellStringValue = cell.getValue().toString()

            val currentCoordinates = Position(x = x, y = rows.lastIndex - y)
            greyedOutPositions?.find { it == currentCoordinates }?.apply {
                cellText += greyBackground
            }
            cellText += when (currentCoordinates) {
                redTarget -> red
                greenTarget -> green
                highlightedPositions?.find { it == currentCoordinates } -> yellow
                else -> ""
            }
            cellText += cellStringValue + reset
            cellText
        }.joinToString(" ")
    }.joinToString("\n"))
}