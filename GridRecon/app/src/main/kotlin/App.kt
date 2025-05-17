package net.penguin.app

import kotlinx.coroutines.runBlocking

val injector = Injector

fun main() = runBlocking {
    val grinInputReader = UserInputReader
    val initialParameters = grinInputReader.getInitialParameters()

    println(initialParameters)

    val grid = injector.provideGridReader().get(initialParameters).await() ?: run {
        println("Error setting up the grid")
        return@runBlocking
    }

    println(grid.toString())
}
