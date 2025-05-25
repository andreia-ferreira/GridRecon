package utils

import entity.Drone
import entity.Position

fun List<Drone.Move>.getPath(): List<Position> {
    return this.map { it.position }
}

fun List<Drone.Move>.getCumulativeScore(): Int {
    return this.last().cumulativeScore
}