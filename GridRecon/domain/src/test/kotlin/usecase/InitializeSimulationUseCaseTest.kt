package usecase

import entity.Drone
import entity.GridType
import entity.Position
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import repository.DroneRepositoryInterface
import repository.GridRepositoryInterface
import utils.SimulationParametersGenerator
import kotlin.test.Test

class InitializeSimulationUseCaseTest {
    private lateinit var gridRepository: GridRepositoryInterface
    private lateinit var droneRepository: DroneRepositoryInterface
    private lateinit var useCase: InitializeSimulationUseCase

    @BeforeEach
    fun setup() {
        gridRepository = mockk(relaxed = true)
        droneRepository = mockk(relaxed = true)
        useCase = InitializeSimulationUseCase(gridRepository, droneRepository)
    }

    @Test
    fun `execute should initialize grid, add drones, and consume cells for each position`() = runTest {
        val positions = listOf(Position(2, 3), Position(1, 1))
        val gridType = GridType.SMALL
        val regenerationRate = 0.5
        val inputParams = SimulationParametersGenerator.generate(
            dronePositions = positions,
            gridType = gridType,
            cellRegenerationRate = regenerationRate
        )
        val requestParams = InitializeSimulationUseCase.RequestParams(inputParams)

        useCase.execute(requestParams)

        coVerifySequence {
            gridRepository.initializeGrid(gridType, regenerationRate)
            for (position in positions) {
                droneRepository.add(any<Drone>(), position)
                gridRepository.consumeCell(position, 0)
            }
        }
    }
}
