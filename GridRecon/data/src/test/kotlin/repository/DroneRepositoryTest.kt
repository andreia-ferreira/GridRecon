package repository

import entity.Drone
import entity.Drone.Move
import entity.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DroneRepositoryTest {

    private lateinit var repository: DroneRepository

    @BeforeEach
    fun setup() {
        repository = DroneRepository()
    }

    @Test
    fun `add should store drone and initialize first move`() {
        val drone = Drone(id = 1)
        val position = Position(2, 3)

        repository.add(drone, position)

        val allDrones = repository.getAllDrones()
        val moves = repository.getMoves(drone.id)

        assertEquals(1, allDrones.size)
        assertEquals(drone, allDrones[0])
        assertEquals(1, moves.size)
        assertEquals(position, moves[0].position)
        assertEquals(0, moves[0].turn)
    }

    @Test
    fun `move should append move to drone's move list`() {
        val drone = Drone(id = 42)
        val start = Position(0, 0)
        val next = Position(1, 1)

        repository.add(drone, start)

        val move = Move(turn = 1, score = 10, position = next, cumulativeScore = 10, parent = null)
        repository.move(drone.id, move)

        val moves = repository.getMoves(drone.id)
        assertEquals(2, moves.size)
        assertEquals(start, moves[0].position)
        assertEquals(next, moves[1].position)
    }

    @Test
    fun `getMoves should return empty list for unknown drone`() {
        val moves = repository.getMoves(-1L)
        assertTrue(moves.isEmpty())
    }

    @Test
    fun `getAllDrones should return all added drones`() {
        val drone1 = Drone(id = 1)
        val drone2 = Drone(id = 2)

        repository.add(drone1, Position(0, 0))
        repository.add(drone2, Position(1, 1))

        val allDrones = repository.getAllDrones()
        assertEquals(2, allDrones.size)
        assertTrue(allDrones.contains(drone1))
        assertTrue(allDrones.contains(drone2))
    }
}
