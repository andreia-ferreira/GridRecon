import algorithm.DroneMovementBeamAlgorithm
import datasource.GridDataSource
import entity.Position
import kotlinx.coroutines.runBlocking
import net.penguin.app.SimulationRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import repository.DroneRepository
import repository.GridRepository
import usecase.*
import utils.InputParamsGenerator
import utils.getCumulativeScore
import utils.getPath
import kotlin.test.Ignore
import kotlin.test.assertEquals

class SimulationIntegrationTest {
    private val mockGridDataSource = mock<GridDataSource>()
    private val droneRepository = DroneRepository()
    private val gridRepository = GridRepository(mockGridDataSource)

    private val algorithmInterface = DroneMovementBeamAlgorithm
    private val getCurrentGridUseCase = GetCurrentGridUseCase(gridRepository)
    private val moveDroneUseCase = MoveDroneUseCase(gridRepository, droneRepository)
    private val getDroneMovesUseCase = GetDroneMovesUseCase(droneRepository)
    private val getAvailableDronesUseCase = GetAvailableDronesUseCase(droneRepository)
    private val initializeGridUseCase = InitializeSimulationUseCase(gridRepository, droneRepository)
    private lateinit var simulationRunner: SimulationRunner

    @BeforeEach
    fun setup() {
        simulationRunner = SimulationRunner(
            initializeSimulationUseCase = initializeGridUseCase,
            getCurrentGridUseCase = getCurrentGridUseCase,
            moveDroneUseCase = moveDroneUseCase,
            getDroneMovesUseCase = getDroneMovesUseCase,
            algorithmInterface = algorithmInterface,
            getAvailableDronesUseCase = getAvailableDronesUseCase
        )
    }

    @Test
    fun `Should move towards a cell with a higher score`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 2)
        val matrixString = listOf(
            "0 0 0",
            "0 0 0",
            "0 0 2"
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)
        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0)
        )
        
        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertEquals(expectedPath, droneMoves.getPath())
        assertEquals(2, droneMoves.getCumulativeScore())
    }

    @Test
    fun `Should prefer higher value path than a lower value`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 1)
        val matrixString = listOf(
            "0 0 0",
            "1 2 0",
            "0 0 0"
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)
        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 1),
        )

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertEquals(expectedPath, droneMoves.getPath())
        assertEquals(2, droneMoves.getCumulativeScore())
    }

    @Ignore
    @Test
    fun `Should choose the most efficient path`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(2,2), maxTurns = 5)
        val matrixString = listOf(
            "0 0 0 0",
            "0 0 0 0",
            "0 0 0 0",
            "1 2 2 2"
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertEquals(7, droneMoves.getCumulativeScore())
        assertTrue(droneMoves.getPath().contains(Position(3,0)))
        
    }

    @Test
    fun `Should move through all cells with high score`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 3)
        val matrixString = listOf(
            "1 1 1 2",
            "1 1 2 0",
            "1 2 1 1",
            "0 1 1 1"
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)
        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 1),
            Position(2, 2),
            Position(3, 3),
        )

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertEquals(expectedPath, droneMoves.getPath())
        assertEquals(6, droneMoves.getCumulativeScore())
    }

    @Test
    fun `Should stay at the start position when there are no more moves left`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 0)
        val matrixString = listOf(
            "1 2 3",
            "4 5 6",
            "7 8 9",
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertEquals(listOf(Position(0, 0)), droneMoves.getPath())
    }

    @Test
    fun `Should stop when the max duration is reached`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(1,1), maxTurns = 50, maxDuration = 100)
        val matrixString: List<String> = List(10) { List(10) { 1 }.joinToString(" ") }
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertTrue(droneMoves.getPath().isNotEmpty())
        assertTrue(simulationRunner.elapsedTime <= inputParams.maxDuration)
    }

    @Test
    fun `Should handle large grid within the time limits`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(1,1), maxTurns = 50, maxDuration = 100)
        val size = 1000
        val matrix = List(size) { i ->
            List(size) { j ->
                if (i == size - 1 && j == size - 1) 100 else 1
            }.joinToString(" ")
        }
        setupMocks(matrix)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertTrue(droneMoves.getPath().size > 1)
        assertTrue (simulationRunner.elapsedTime <= inputParams.maxDuration)
    }

    @Test
    fun `Should move even if the grid has zero score values`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(1,1), maxTurns = 3)
        val matrixString: List<String> = List(10) { List(10) { 0 }.joinToString(" ") }
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertTrue(droneMoves.getPath().size > 1)
        assertEquals (0, droneMoves.getCumulativeScore())
    }

    @Test
    fun `Should handle spiral pattern efficiently`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 8)
        val matrixString = listOf(
            "1 1 1 1 1",
            "1 5 5 5 1",
            "1 5 9 5 1",
            "1 5 5 5 1",
            "1 1 1 1 1",
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertTrue(droneMoves.getPath().contains(Position(2, 2)))
        assertTrue (droneMoves.getCumulativeScore() > 20)
    }

    @Test
    fun `Should revisit high-value regenerating cells`() = runBlocking {
        val inputParams = InputParamsGenerator.generate(dronePosition = Position(0,0), maxTurns = 4, cellRegenerationRate = 0.5)
        val matrixString = listOf(
            "0 0 0",
            "0 10 0",
            "0 0 0",
        )
        setupMocks(matrixString)

        simulationRunner.execute(inputParams)

        val droneMoves = getDroneMovesUseCase.execute(GetDroneMovesUseCase.RequestParams(0))
        assertTrue(droneMoves.getPath().contains(Position(1, 1)))
        assertTrue (droneMoves.getCumulativeScore() > 10)
    }

    private suspend fun setupMocks(matrix: List<String>) {
        whenever(mockGridDataSource.getFileContents(any())).thenReturn(matrix)
    }
}