package net.penguin.app

import kotlinx.coroutines.runBlocking
import net.penguin.domain.usecase.GetSimulationUseCase

val injector = Injector
val getSimulationUseCase = injector.provideGetSimulationUseCase()

fun main() = runBlocking {
    val grinInputReader = UserInputReader
    val initialParameters = grinInputReader.getInitialParameters()

    println(initialParameters)

    val simulation = getSimulationUseCase.execute(GetSimulationUseCase.RequestParams(initialParameters)) ?: run {
        println("Error setting up the grid")
        return@runBlocking
    }

    println(simulation.toString())
}
