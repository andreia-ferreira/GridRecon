package net.penguin.app

import net.penguin.domain.entity.GridType
import net.penguin.domain.entity.InputParams
import net.penguin.domain.entity.Position

object UserInputReader {
    fun getInitialParameters(): InputParams {
        val cellRegenerationRate = 0.5
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
        val dronePosition = requestInput(
            prompt = "Please insert the initial drone coordinates separated by comma (x, y): ",
            parser = { input ->
                val coordinates = input?.split(",")?.map { it.trim().toIntOrNull() }
                    ?: throw NumberFormatException()
                if (coordinates.size != 2 || coordinates.any { it == null }) {
                    throw IllegalArgumentException("Please enter exactly two valid integers sepparated by a comma.")
                }
                val (x, y) = coordinates.filterNotNull()
                if (x >= gridType.size || y >= gridType.size) {
                    throw IllegalArgumentException("Coordinates must be within grid bounds.")
                }
                Position(x, y)
            }
        )

        return InputParams(
            gridType = gridType,
            maxSteps = maxSteps,
            maxDuration = maxDuration,
            dronePosition = dronePosition,
            cellRegenerationRate = cellRegenerationRate
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