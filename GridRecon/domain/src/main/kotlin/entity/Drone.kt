package net.penguin.domain.entity

class Drone(
    initialPosition: Position,
) {
    data class Move(
        val turn: Int,
        val score: Int,
        val position: Position,
        val cumulativeScore: Int,
        val parent: Move? = null
    )

    private val currentMoves = mutableListOf(Move(0, 0, initialPosition, 0))

    fun getCurrentPosition(): Position {
        return currentMoves.last().position
    }

    fun move(move: Move) {
        currentMoves.add(move)
    }

    fun getCumulativeScore(): Int {
        return currentMoves.last().cumulativeScore
    }

    fun getPath(): List<Position> {
        return currentMoves.map { it.position }
    }

    fun getAllMovesData(): List<Move> {
        return currentMoves
    }
}