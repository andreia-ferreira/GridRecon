package net.penguin.domain.entity

data class Cell(
    val initialValue: Int
) {
    var currentValue = initialValue
        private set

    fun consume(): Int {
        return currentValue.also {
            currentValue = 0
        }
    }

    fun regenerate(rate: Int) {
        if (currentValue < initialValue) {
            currentValue = currentValue + rate
        }
    }

    fun valueAtTimeStep(
        timeStep: Int,
        regenerationRate: Int
    ): Int {
        return minOf(currentValue + timeStep * regenerationRate, initialValue)
    }

    override fun toString(): String {
        return "$currentValue"
    }
}
