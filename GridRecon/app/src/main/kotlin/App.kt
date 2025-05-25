package net.penguin.app

import algorithm.DroneMovementBeamAlgorithm
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val injector = Injector

    val grinInputReader = UserInputReader
    val initialParameters = grinInputReader.getInitialParameters()

    println(initialParameters)

    SimulationRunner(
        algorithmInterface = DroneMovementBeamAlgorithm,
        getCurrentGridUseCase = injector.provideGetCurrentGridUseCase(),
        getDroneMovesUseCase = injector.provideGetDroneMovesUseCase(),
        initializeSimulationUseCase = injector.provideInitializeGridUseCase(),
        moveDroneUseCase = injector.provideMoveDroneUseCase(),
        getAvailableDronesUseCase = injector.provideGetAvailableDronesUseCase()
    ).execute(simulationParameters = initialParameters)
}
