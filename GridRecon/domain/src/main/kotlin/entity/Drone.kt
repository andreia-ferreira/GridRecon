package entity

data class Drone(
    val id: Long = 0
) {
    data class Move(
        val turn: Int,
        val score: Int,
        val position: Position,
        val cumulativeScore: Int,
        val parent: Move? = null
    )
}