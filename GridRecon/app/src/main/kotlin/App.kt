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
        getAllDroneMovesUseCase = injector.provideGetAllDroneMovesUseCase(),
        initializeSimulationUseCase = injector.provideInitializeGridUseCase(),
        moveDroneUseCase = injector.provideMoveDroneUseCase(),
        getAvailableDronesUseCase = injector.provideGetAvailableDronesUseCase(),
        getLatestDroneMovesUseCase = injector.provideGetLatestDroneMoveUseCase()
    ).execute(simulationParameters = initialParameters)
}
