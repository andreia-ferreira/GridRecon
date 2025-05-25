package algorithm

import entity.Drone
import entity.Position
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import utils.GridGenerator
import utils.SimulationParametersGenerator

class DroneMovementBeamAlgorithmTest {

    @Test
    fun `getCandidates return all valid neighbor moves in 8 directions`() {
        val currentMove = Drone.Move(turn = 0, score = 0, cumulativeScore = 0, position = Position(1, 1))
        val inputParams = SimulationParametersGenerator.generate(dronePosition = Position(0, 0))
        val matrix = listOf(
            mutableListOf(0, 1, 0),
            mutableListOf(0, 0, 0),
            mutableListOf(0, 0, 0),
        )
        val grid = GridGenerator.generate(matrix, inputParams.cellRegenerationRate)

        val result = DroneMovementBeamAlgorithm.getCandidates(currentMove, grid, inputParams)
        val candidatePositions = result.map { it.first.position }

        assertTrue(candidatePositions.containsAll(listOf(
            Position(2, 1), Position(2, 2), Position(2, 0), Position(1, 0),
            Position(0, 0), Position(0, 1), Position(1, 2), Position(0, 2)
        )))
        assertEquals(8, result.size)
    }

    @Test
    fun `getCandidates return only valid neighbor moves if the drone is on the edge of the grid`() {
        val currentMove = Drone.Move(turn = 0, score = 0, cumulativeScore = 0, position = Position(2, 0))
        val inputParams = SimulationParametersGenerator.generate(dronePosition = Position(2, 0))
        val matrix = listOf(
            mutableListOf(0, 1, 0),
            mutableListOf(0, 0, 0),
            mutableListOf(0, 0, 0),
        )
        val grid = GridGenerator.generate(matrix, inputParams.cellRegenerationRate)

        val result = DroneMovementBeamAlgorithm.getCandidates(currentMove, grid, inputParams)
        val candidatePositions = result.map { it.first.position }

        assertTrue(candidatePositions.containsAll(listOf(
            Position(2, 1), Position(1, 0), Position(1, 1)
        )))
        assertEquals(3, result.size)
    }

    @Test
    fun `getNextBestMove should select the neighbor with highest total score`() {
        val currentMove = Drone.Move(turn = 0, score = 0, cumulativeScore = 0, position = Position(1, 1))
        val inputParams = SimulationParametersGenerator.generate(dronePosition = Position(0, 0))
        val matrix = listOf(
            mutableListOf(0, 1, 0),
            mutableListOf(0, 0, 0),
            mutableListOf(0, 0, 0),
        )
        val grid = GridGenerator.generate(matrix, inputParams.cellRegenerationRate)

        val candidates = DroneMovementBeamAlgorithm.getCandidates(currentMove, grid, inputParams)
        val move = DroneMovementBeamAlgorithm.getNextBestMove(currentMove, candidates, inputParams)

        assertEquals(Position(1, 2), move!!.position)
    }

    @Test
    fun `getNextBestMove should return null if no valid neighbor exists`() {
        val matrix = listOf(mutableListOf(0))
        val currentMove = Drone.Move(turn = 0, score = 0, cumulativeScore = 0, position = Position(0, 0))
        val inputParams = SimulationParametersGenerator.generate(dronePosition = Position(1, 1))
        val grid = GridGenerator.generate(matrix, inputParams.cellRegenerationRate)

        val candidates = DroneMovementBeamAlgorithm.getCandidates(currentMove, grid, inputParams)
        val move = DroneMovementBeamAlgorithm.getNextBestMove(currentMove, candidates, inputParams)

        assertNull(move)
    }
}
