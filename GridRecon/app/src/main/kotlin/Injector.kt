package net.penguin.app

import datasource.GridDataSource
import repository.DroneRepository
import repository.DroneRepositoryInterface
import repository.GridRepository
import repository.GridRepositoryInterface
import usecase.*

object Injector {
    private val gridRepository: GridRepositoryInterface by lazy {
        GridRepository(GridDataSource)
    }
    private val droneRepository: DroneRepositoryInterface by lazy {
        DroneRepository()
    }

    fun provideInitializeGridUseCase(): InitializeSimulationUseCase {
        return InitializeSimulationUseCase(gridRepository, droneRepository)
    }

    fun provideMoveDroneUseCase(): MoveDroneUseCase {
        return MoveDroneUseCase(gridRepository, droneRepository)
    }

    fun provideGetCurrentGridUseCase(): GetCurrentGridUseCase {
        return GetCurrentGridUseCase(gridRepository)
    }

    fun provideGetAllDroneMovesUseCase(): GetAllDroneMovesUseCase {
        return GetAllDroneMovesUseCase(droneRepository)
    }

    fun provideGetAvailableDronesUseCase(): GetAvailableDronesUseCase {
        return GetAvailableDronesUseCase(droneRepository)
    }

    fun provideGetLatestDroneMoveUseCase(): GetLatestDroneMovesUseCase {
        return GetLatestDroneMovesUseCase(droneRepository)
    }
}