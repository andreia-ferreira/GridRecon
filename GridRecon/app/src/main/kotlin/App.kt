package net.penguin.app

import kotlinx.coroutines.runBlocking

val injector = Injector

fun main() = runBlocking {
    val grid = injector.provideGridReader().get().await()

    println(grid.toString())
}
