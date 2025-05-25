package repository

import entity.Drone
import entity.Drone.Move
import entity.Position

class DroneRepository: DroneRepositoryInterface {
    private val drones = mutableListOf<Drone>()
    private val droneMoves = mutableMapOf<Long, List<Move>>()

    override fun add(drone: Drone, position: Position) {
        this.drones.add(drone)
        droneMoves[drone.id] = listOf(
            Move(
                turn = 0,
                score = 0,
                position = position,
                cumulativeScore = 0,
                parent = null
            )
        )
    }

    override fun move(droneId: Long, move: Move) {
        droneMoves[droneId]?.let {
            droneMoves[droneId] = it + move
        }
    }

    override fun getMoves(droneId: Long): List<Move> {
        return droneMoves[droneId].orEmpty()
    }

    override fun getAllDrones(): List<Drone> {
        return drones.toList()
    }
}