package net.penguin.domain.entity

class Simulation(
    val maxMoves: Int,
    val maxDuration: Long,
    val grid: Grid,
    val startPosition: Position
) {
    var startTime: Long = -1
        private set
    var currentTurn: Int = 0
        private set

    fun startTimer() {
        startTime = System.currentTimeMillis()
    }

    fun nextTurn() = currentTurn ++

    fun isTimeLimitReached(): Boolean {
        return System.currentTimeMillis() - startTime >= maxDuration
    }

    fun isMovementLimitReached(): Boolean {
        return currentTurn == maxMoves
    }
}