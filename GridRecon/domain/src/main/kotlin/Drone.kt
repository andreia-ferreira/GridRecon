package net.penguin.domain

class Drone(initialPosition: Position) {
    var currentPosition: Position = initialPosition
        private set
    val path = mutableListOf(initialPosition)

    fun moveTo(position: Position) {
        this.currentPosition = position
        path.add(position)
    }
}
