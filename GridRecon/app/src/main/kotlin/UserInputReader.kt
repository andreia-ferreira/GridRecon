package net.penguin.app

import net.penguin.domain.InitialParameters

object UserInputReader {
    fun getInitialParameters(): InitialParameters {
        val gridSize = 20
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
        val droneCoordinates = requestInput(
            prompt = "Please insert the initial drone coordinates (x, y): ",
            parser = { input ->
                try {
                    input!!.split(",")
                        .map { it.trim().toInt() }
                        .takeIf { it.size == 2 && it[0] <= gridSize && it[1] <= gridSize }!!
                        .let { Pair(it[0], it[1]) }
                } catch (_: Exception) {
                    println("Please introduce valid coordinates separated by a comma")
                    null
                }
            }
        )

        return InitialParameters(
            gridSize,
            maxSteps,
            maxDuration,
            droneCoordinates
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