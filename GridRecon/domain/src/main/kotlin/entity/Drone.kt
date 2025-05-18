package net.penguin.domain.entity

class Drone(initialPosition: Position) {
    val path = mutableListOf(initialPosition)
    var currentPosition: Position = initialPosition
        private set

    fun moveTo(position: Position) {
        currentPosition = position
        path.add(position)

    }
}
