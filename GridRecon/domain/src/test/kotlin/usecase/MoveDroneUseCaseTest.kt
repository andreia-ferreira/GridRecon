package usecase

import entity.Drone
import entity.Position
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import repository.DroneRepositoryInterface
import repository.GridRepositoryInterface
import utils.getPath

class MoveDroneUseCaseTest {

    private lateinit var gridRepository: GridRepositoryInterface
    private lateinit var droneRepository: DroneRepositoryInterface
    private lateinit var useCase: MoveDroneUseCase

    @BeforeEach
    fun setup() {
        gridRepository = mockk(relaxed = true)
        droneRepository = mockk(relaxed = true)
        useCase = MoveDroneUseCase(gridRepository, droneRepository)
    }

    @Test
    fun `Should regenerate, move, and consume cell correctly`() = runTest {
        val droneId = 0L
        val currentTurn = 1
        val currentPosition = Position(5,5)
        val newMove = Drone.Move(turn = currentTurn, score = 5, position = currentPosition, cumulativeScore = 5)

        val currentMoves = listOf(Drone.Move(turn = 0, score = 0, position = Position(4,4), cumulativeScore = 0))
        coEvery { droneRepository.getMoves(droneId) } returns currentMoves

        val requestParams = MoveDroneUseCase.RequestParams(
            droneId = droneId,
            droneMove = newMove
        )

        useCase.execute(requestParams)

        coVerifySequence {
            droneRepository.getMoves(droneId)
            gridRepository.regenerateCell(currentMoves.getPath(), currentTurn)
            droneRepository.move(droneId, newMove)
            gridRepository.consumeCell(currentPosition, currentTurn)
        }
    }
}
