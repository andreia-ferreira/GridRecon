package net.penguin.app

import entity.GridType
import entity.Position
import entity.SimulationParameters

object UserInputReader {
    fun getInitialParameters(): SimulationParameters {
        val gridType: GridType = requestInput(
            prompt = "Which grid size would you like to use?\n" +
                    GridType.entries.mapIndexed { index, type ->
                        "${index + 1} - ${type.size} x ${type.size}\n"
                    }.joinToString("") + "> ",
            parser = { input ->
                val index = input?.toIntOrNull()?.minus(1) ?: throw NumberFormatException()
                GridType.entries.getOrNull(index) ?: throw IndexOutOfBoundsException()
            }
        )

        val maxSteps: Int = requestInput(
            prompt = "Please insert the number of time steps (t): ",
            parser = { input ->
                val value = input?.toIntOrNull() ?: throw NumberFormatException()
                if (value <= 0) throw IllegalArgumentException("Number must be greater than 0.")
                value
            }
        )

        val maxDuration = requestInput(
            prompt = "Please insert the maximum duration in milliseconds (T): ",
            parser = { input ->
                val value = input?.toLongOrNull() ?: throw NumberFormatException()
                if (value <= 0) throw IllegalArgumentException("Duration must be greater than 0.")
                value
            }
        )

        val dronePositions = mutableListOf<Position>()
        do {
            val index = dronePositions.size + 1
            val position = requestInput(
                prompt = "Please insert the coordinates for drone #$index separated by a comma (x, y): ",
                parser = { input ->
                    val coordinates =
                        input?.split(",")?.map { it.trim().toIntOrNull() } ?: throw NumberFormatException()
                    if (coordinates.size != 2 || coordinates.any { it == null }) {
                        throw IllegalArgumentException("Please enter exactly two valid integers separated by a comma.")
                    }
                    val (x, y) = coordinates.filterNotNull()
                    if (x >= gridType.size || y >= gridType.size || x < 0 || y < 0) {
                        throw IllegalArgumentException("Coordinates must be within grid bounds.")
                    }
                    val result = Position(x, y)
                    if (result in dronePositions) {
                        throw IllegalArgumentException("This position has already been taken by another drone.")
                    }
                    result
                }
            )
            dronePositions.add(position)

            if (dronePositions.size >= 10) break // limit to prevent infinite spam

            println("Add another drone? (y/n): ")
            val continueInput = readLine()?.trim()?.lowercase()
        } while (continueInput == "y" || continueInput == "yes")

        return SimulationParameters(
            gridType = gridType,
            maxTurns = maxSteps,
            maxDuration = maxDuration,
            dronePositions = dronePositions,
            cellRegenerationRate = 0.25,
            printIntermediateSteps = gridType == GridType.SMALL // printing to the console can have great impact with bigger grids
        )
    }

    private fun <T> requestInput(prompt: String, parser: (String?) -> T): T {
        while (true) {
            print(prompt)
            val input = readLine()
            try {
                return parser(input)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(e: Exception) {
        when (e) {
            is IndexOutOfBoundsException -> println("Please enter a number within the valid range")
            is NumberFormatException -> println("Please introduce a valid number")
            is NullPointerException -> println("Please provide an input.")
            is IllegalArgumentException -> println(e.message)
        }
    }
}