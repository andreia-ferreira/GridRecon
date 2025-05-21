package net.penguin.domain.entity

data class Cell(
    val initialValue: Int
) {
    private var regenerationProgress: Double = initialValue.toDouble()
    private var timeStepLastVisited: Int = -1

    fun getValue(): Int = regenerationProgress.toInt()

    fun consume(timeStep: Int): Int {
        return if (timeStepLastVisited != timeStep) {
            timeStepLastVisited = timeStep
            val consumed = regenerationProgress.toInt()
            regenerationProgress -= consumed
            consumed
        } else 0
    }

    fun regenerate(rate: Double, timeStep: Int) {
        if (getValue() < initialValue && timeStep != timeStepLastVisited) {
            regenerationProgress = minOf(initialValue.toDouble(), regenerationProgress + rate)
        }
    }

    override fun toString(): String {
        return "${getValue()}"
    }

    fun copyForClone(): Cell {
        val clone = Cell(initialValue)
        clone.regenerationProgress = this.regenerationProgress
        clone.timeStepLastVisited = this.timeStepLastVisited
        return clone
    }

}
