package net.penguin.app

import kotlinx.coroutines.runBlocking
import net.penguin.domain.algorithm.DroneMovementBeamAlgorithm
import net.penguin.domain.usecase.GetSimulationUseCase

fun main() = runBlocking {
    val injector = Injector
    val getSimulationUseCase = injector.provideGetSimulationUseCase()

    val grinInputReader = UserInputReader
    val initialParameters = grinInputReader.getInitialParameters()

    println(initialParameters)

    val simulation = getSimulationUseCase.execute(
        GetSimulationUseCase.RequestParams(initialParameters)
    ) ?: run {
        println("Error setting up the grid")
        return@runBlocking
    }

    SimulationRunner(DroneMovementBeamAlgorithm).execute(simulation)
}
