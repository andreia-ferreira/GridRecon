package net.penguin.domain

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

    override fun toString(): String {
        return "$currentValue"
    }
}
