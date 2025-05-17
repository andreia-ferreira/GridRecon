package net.penguin.domain

data class Cell(
    private val initialValue: Int
) {
    private var currentValue = initialValue

    fun regenerate(rate: Int) {
        if (currentValue < initialValue) {
            currentValue = currentValue + rate
        }
    }

    override fun toString(): String {
        return "$currentValue"
    }
}
