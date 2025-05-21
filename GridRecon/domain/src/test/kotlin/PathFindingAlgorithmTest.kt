import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.penguin.domain.algorithm.PathFindingAlgorithm
import net.penguin.domain.algorithm.SearchState
import net.penguin.domain.entity.Cell
import net.penguin.domain.entity.Grid
import net.penguin.domain.entity.Position
import net.penguin.domain.entity.Simulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PathFindingAlgorithmTest {
    @Test
    fun `should find straight path in empty grid`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 2),
            listOf(0, 0, 2),
            listOf(0, 0, 2),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            moves = 2,
            maxDuration = 1000,
            startPosition = Position(0,0)
        )

        val result = PathFindingAlgorithm.run(simulation)

        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0)
        )

        assertEquals(expectedPath, ((result.first { it is SearchState.Result } as SearchState.Result).path))
    }

    @Test
    fun `should prefer higher value path`() = runBlocking {
        val matrix = listOf(
            listOf(0, 0, 0),
            listOf(1, 2, 0),
            listOf(0, 0, 0),
        )
        val simulation = Simulation(
            grid = Grid(matrix.map { it.map { Cell(it) }}),
            moves = 1,
            maxDuration = 1000,
            startPosition = Position(0,0)
        )

        val result = PathFindingAlgorithm.run(simulation)

        val expectedPath = listOf(
            Position(0, 0),
            Position(1, 1),
        )

        assertEquals(expectedPath, ((result.first { it is SearchState.Result } as SearchState.Result).path))
    }
}