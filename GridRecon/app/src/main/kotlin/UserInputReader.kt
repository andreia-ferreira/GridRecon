package net.penguin.app

import net.penguin.domain.entity.InputParams
import net.penguin.domain.entity.Position

object UserInputReader {
    fun getInitialParameters(): InputParams {
        val gridSize = 20 // TODO make this dynamic too
        val cellRegenerationRate = 0.5
        val maxSteps: Int = requestInput(
            prompt = "Please insert the number of time steps (t): ",
            parser = { input ->
                try {
                    input!!.toInt().takeIf { it > 0 }
                } catch (_: Exception) {
                    println("Please introduce a valid number")
                    null
                }
            }
        )

        val maxDuration = requestInput(
            prompt = "Please insert the maximum duration in milliseconds (T): ",
            parser = { input ->
                try {
                    input!!.toLong().takeIf { it > 0L }
                } catch (_: Exception) {
                    println("Please introduce a valid number")
                    null
                }
            }
        )
        val dronePosition = requestInput(
            prompt = "Please insert the initial drone coordinates separated by comma (x, y): ",
            parser = { input ->
                try {
                    input!!.split(",")
                        .map { it.trim().toInt() }
                        .takeIf { it.size == 2 && it[0] < gridSize && it[1] < gridSize }!!
                        .let {
                            Position(it[0], it[1])
                        }
                } catch (_: Exception) {
                    println("Please introduce valid coordinates separated by a comma")
                    null
                }
            }
        )

        return InputParams(
            gridSize,
            maxSteps,
            maxDuration,
            dronePosition,
            cellRegenerationRate
        )
    }

    private fun <T> requestInput(prompt: String, parser: (String?) -> T?): T {
        while (true) {
            print(prompt)
            val input = parser(readLine())
            if (input != null) {
                return input
            }
        }
    }
}