package entity

data class Cell(
    val maxValue: Int,
    val currentValue: Double,
    val regenerationRate: Double = 0.0,
    val turnLastVisited: Int = -1
) {
    fun getValue(): Int = currentValue.toInt()

    fun canRegenerate(turn: Int): Boolean {
        return currentValue < maxValue.toDouble() && turn != turnLastVisited
    }

    fun estimateValueAt(currentTurn: Int, targetTurn: Int): Int {
        if (getValue() == maxValue) return getValue()
        val elapsedTurns = targetTurn - currentTurn
        val projectedValue = getValue() + (regenerationRate * elapsedTurns)
        return minOf(maxValue, projectedValue.toInt())
    }
}
