package net.penguin.domain.entity

class Cell(
    val initialValue: Int,
    val regenerationRate: Double = 0.0
) {
    private var currentValue: Double = initialValue.toDouble()
    private var turnLastVisited: Int = -1

    fun getValue(): Int = currentValue.toInt()

    fun consume(turn: Int) {
        if (turnLastVisited != turn) {
            turnLastVisited = turn
            val consumed = currentValue.toInt()
            currentValue -= consumed
            consumed
        }
    }

    fun regenerate(turn: Int) {
        if (getValue() < initialValue && turn != turnLastVisited) {
            currentValue = minOf(initialValue.toDouble(), currentValue + regenerationRate)
        }
    }

    fun estimateValueAt(currentTurn: Int, targetTurn: Int): Int {
        if (getValue() == initialValue) return getValue()
        val elapsedTurns = targetTurn - currentTurn
        val projectedValue = getValue() + (regenerationRate * elapsedTurns)
        return minOf(initialValue, projectedValue.toInt())
    }
}
