package net.penguin.domain

class Drone(
    private var position: Position
) {
    fun moveTo(position: Position) {
        this.position = position
    }

    fun matchesPosition(position: Position): Boolean {
        return this.position.x == position.x && this.position.y == position.y
    }
}
