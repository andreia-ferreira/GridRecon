package net.penguin.domain.entity

data class Cell(
    val initialValue: Int
) {
    private var regenerationProgress: Double = initialValue.toDouble()
    private var turnLastVisited: Int = -1

    fun getValue(): Int = regenerationProgress.toInt()

    fun consume(turn: Int): Int {
        return if (turnLastVisited != turn) {
            turnLastVisited = turn
            val consumed = regenerationProgress.toInt()
            regenerationProgress -= consumed
            consumed
        } else 0
    }

    fun regenerate(rate: Double, turn: Int) {
        if (getValue() < initialValue && turn != turnLastVisited) {
            regenerationProgress = minOf(initialValue.toDouble(), regenerationProgress + rate)
        }
    }
}
