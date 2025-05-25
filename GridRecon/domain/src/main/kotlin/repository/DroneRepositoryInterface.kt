package repository

import entity.Drone
import entity.Drone.Move
import entity.Position

interface DroneRepositoryInterface {
    fun add(drone: Drone, position: Position)
    fun move(droneId: Long, move: Move)
    fun getMoves(droneId: Long): List<Move>
    fun getAllDrones(): List<Drone>
}