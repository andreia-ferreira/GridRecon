package net.penguin.app

import net.penguin.data.GridFileReader
import net.penguin.domain.GridReaderInterface
import net.penguin.domain.usecase.GetOptimalPathUseCase
import net.penguin.domain.usecase.GetSimulationUseCase

object Injector {
    private fun provideGridReader(): GridReaderInterface {
        return GridFileReader
    }

    fun provideGetSimulationUseCase(): GetSimulationUseCase {
        return GetSimulationUseCase(
            provideGridReader()
        )
    }

    fun provideGetOptimalPathUseCase(): GetOptimalPathUseCase {
        return GetOptimalPathUseCase()
    }
}