package net.penguin.domain.entity

class Simulation(
    val maxMoves: Int,
    val maxDuration: Long,
    val grid: Grid,
    val startPosition: Position
) {
    val drone = Drone(startPosition)

    var startTime: Long = -1
        private set
    var currentTurn: Int = 0
        private set

    fun startTimer() {
        startTime = System.currentTimeMillis()
    }

    fun nextTurn() {
        currentTurn ++
        grid.regenerateCells(currentTurn)
    }

    fun isTimeLimitReached(): Boolean {
        return System.currentTimeMillis() - startTime >= maxDuration
    }

    fun isMovementLimitReached(): Boolean {
        return currentTurn == maxMoves
    }

    fun moveDrone(move: Drone.Move) {
        drone.move(move)
        grid.getCell(move.position).consume(move.turn)
    }
}