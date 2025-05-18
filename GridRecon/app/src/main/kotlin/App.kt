package net.penguin.app

import kotlinx.coroutines.runBlocking
import net.penguin.domain.usecase.GetOptimalPathUseCase
import net.penguin.domain.usecase.GetSimulationUseCase

val injector = Injector
val getSimulationUseCase = injector.provideGetSimulationUseCase()
val getOptimalPathUseCase = injector.provideGetOptimalPathUseCase()

fun main() = runBlocking {
    val grinInputReader = UserInputReader
    val initialParameters = grinInputReader.getInitialParameters()

    println(initialParameters)

    val simulation = getSimulationUseCase.execute(GetSimulationUseCase.RequestParams(initialParameters)) ?: run {
        println("Error setting up the grid")
        return@runBlocking
    }

    val result = getOptimalPathUseCase.execute(GetOptimalPathUseCase.RequestParams(simulation))

    println("=== Simulation Steps ===")
    simulation.runSimulationWithPath(
        path = result.path,
        onCellValueConsumed = {
            println("Step ${simulation.currentTimeStep} - Drone at ${simulation.drone.currentPosition} consumed $it")
            println(simulation)
        }
    )

    println("=== Final Result ===")
    println("Total Score: ${result.totalScore}")
    println("Time Steps Used: ${result.path.size}")
}
