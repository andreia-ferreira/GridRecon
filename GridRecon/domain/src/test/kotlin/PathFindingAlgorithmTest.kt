import kotlinx.coroutines.runBlocking
import net.penguin.domain.algorithm.DroneMovementBeamAlgorithm
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Cell
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PathFindingAlgorithmTest {
    @Test
    fun `Should move towards a cell with a higher score`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 0),
            listOf(0, 0, 0),
            listOf(0, 0, 2),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 2,
            maxDuration = 1000,
            startPosition = Position(0,0)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0)
        )

        assertEquals(expectedPath, result.path)
        assertEquals(2, result.totalScore)
    }

    @Test
    fun `Should prefer higher value path`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 0),
            listOf(1, 2, 0),
            listOf(0, 0, 0),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 1,
            maxDuration = 1000,
            startPosition = Position(0,0)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 1),
        )

        assertEquals(expectedPath, result.path)
        assertEquals(2, result.totalScore)
    }

    @Test
    fun `Should move through all cells with high score`() = runBlocking {
        val matrix = listOf(
            listOf(1, 1, 1, 2),
            listOf(1, 1, 2, 0),
            listOf(1, 2, 1, 1),
            listOf(0, 1, 1, 1),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 3,
            maxDuration = 1000,
            startPosition = Position(0,0)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 1),
            Position(2, 2),
            Position(3, 3),
        )

        assertEquals(expectedPath, result.path)
        assertEquals(6, result.totalScore)
    }

    @Test
    fun `Should stay at the start position when there are no more moves left`() = runBlocking {
        val matrix = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 0,
            maxDuration = 1000,
            startPosition = Position(1, 1)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        assertEquals(listOf(Position(1, 1)), result.path)
    }

    @Test
    fun `Should stop when the max duration is reached`() = runBlocking {
        val matrix = List(10) { List(10) { 1 } }
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 50,
            maxDuration = 100,
            startPosition = Position(0, 0)
        )

        val startTime = System.currentTimeMillis()
        val result = DroneMovementBeamAlgorithm.run(simulation, {})
        val endTime = System.currentTimeMillis()

        assertTrue(result.path.isNotEmpty())
        assertTrue(endTime - startTime < simulation.maxDuration)
    }

    @Test
    fun `Should handle large grid within the time limits`() = runBlocking {
        val size = 1000
        val matrix = List(size) { i ->
            List(size) { j ->
                if (i == size - 1 && j == size - 1) 100 else 1
            }
        }
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 3,
            maxDuration = 1000,
            startPosition = Position(0, 0)
        )

        val startTime = System.currentTimeMillis()
        val result = DroneMovementBeamAlgorithm.run(simulation, {})
        val endTime = System.currentTimeMillis()

        assertTrue(endTime - startTime < simulation.maxDuration)
        assertTrue(result.path.isNotEmpty())
    }

    @Test
    fun `Should move even if the grid has zero score values`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 0),
            listOf(0, 0, 0),
            listOf(0, 0, 0),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 3,
            maxDuration = 1000,
            startPosition = Position(1, 1)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        assertTrue(result.path.size > 1)
        assertEquals(0, result.totalScore)
    }

    @Test
    fun `Should handle spiral pattern efficiently`() = runBlocking {
        val matrix = listOf(
            listOf(1, 1, 1, 1, 1),
            listOf(1, 5, 5, 5, 1),
            listOf(1, 5, 9, 5, 1),
            listOf(1, 5, 5, 5, 1),
            listOf(1, 1, 1, 1, 1),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            maxMoves = 8,
            maxDuration = 1000,
            startPosition = Position(0, 0)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {})

        assertTrue(result.path.contains(Position(2, 2)))
        assertTrue(result.totalScore > 20)
    }

    @Test
    fun `Should revisit high-value regenerating cells`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 0),
            listOf(0, 10, 0),
            listOf(0, 0, 0),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}, regenerationRate = 0.5),
            maxMoves = 4,
            maxDuration = 1000,
            startPosition = Position(0, 0)
        )

        val result = DroneMovementBeamAlgorithm.run(simulation, {
            if (it is SearchState.Move) {
                simulation.grid.regenerateCells(it.dronePosition.currentTurn)
                simulation.grid.getCell(it.dronePosition.position).consume(it.dronePosition.currentTurn)
            }
        })

        assertTrue(result.path.contains(Position(1, 1)))
        assertTrue(result.totalScore > 10)
    }
}