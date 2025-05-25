package net.penguin.app

import entity.Grid
import entity.Position

object ConsolePrintUtils {
    private const val GREEN = "\u001B[32m"
    private const val RED = "\u001B[31m"
    private const val YELLOW = "\u001B[33m"
    private const val DARK_GREY_BACKGROUND = "\u001B[48;5;232m"
    private const val RESET = "\u001B[0m"

    fun printGrid(
        grid: Grid,
        redTargets: List<Position>? = null,
        greenTargets: List<Position>? = null,
        highlightedPositions: List<Position>? = null,
        greyedOutPositions: List<Position>? = null
    ) {
        getStyledLines(
            grid = grid,
            redTargets = redTargets,
            greenTargets = greenTargets,
            highlightedPositions = highlightedPositions,
            greyedOutPositions = greyedOutPositions
        ).forEach { println(it) }
    }

    fun printSideBySideGrids(
        initialGrid: Grid?,
        finalGrid: Grid,
        redInitials: List<Position>? = null,
        redFinals: List<Position>? = null,
        highlightedPositions: List<Position> = emptyList(),
        explored: List<Position> = emptyList()
    ) {
        initialGrid?.let {
            val leftLines = getStyledLines(
                grid = initialGrid,
                redTargets = redInitials,
                highlightedPositions = highlightedPositions
            )

            val rightLines = getStyledLines(
                grid = finalGrid,
                redTargets = redFinals,
                highlightedPositions = highlightedPositions,
                greyedOutPositions = explored
            )

            println("Initial Grid VS Final Grid")
            for ((left, right) in leftLines.zip(rightLines)) {
                println("$left   |  $right")
            }
        } ?: getStyledLines(
            grid = finalGrid,
            redTargets = redFinals,
            highlightedPositions = highlightedPositions,
            greyedOutPositions = explored
        ).forEach { println(it) }
    }

    private fun getStyledLines(
        grid: Grid,
        redTargets: List<Position>? = null,
        greenTargets: List<Position>? = null,
        highlightedPositions: List<Position>? = null,
        greyedOutPositions: List<Position>? = null
    ): List<String> {
        return grid.rows.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                var cellText = ""
                val cellStringValue = "%2d".format(cell.getValue())
                val currentCoordinates = Position(x = x, y = grid.rows.lastIndex - y)

                greyedOutPositions?.find { it == currentCoordinates }?.apply {
                    cellText += DARK_GREY_BACKGROUND
                }

                cellText += when (currentCoordinates) {
                    redTargets?.find { it == currentCoordinates } -> RED
                    greenTargets?.find { it == currentCoordinates } -> GREEN
                    highlightedPositions?.find { it == currentCoordinates } -> YELLOW
                    else -> ""
                }

                cellText + cellStringValue + RESET
            }.joinToString(" ")
        }
    }
}
