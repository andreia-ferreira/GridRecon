package net.penguin.domain.entity

class Simulation(
    val maxSteps: Int,
    val maxDuration: Long,
    val grid: Grid,
    val drone: Drone
) {
    var currentTimeStep = 0

    fun runSimulationWithPath(path: List<TimedPosition>, onCellValueConsumed: (Int) -> Unit) {
        for ((step, timedPos) in path.withIndex()) {
            currentTimeStep = step

            // Move drone
            drone.moveTo(timedPos.position)

            // Consume the cell the drone moves to
            val consumedValue = grid.getCell(timedPos.position).consume()
            onCellValueConsumed(consumedValue)

            grid.regenerateCells()
        }
    }

    override fun toString(): String {
        val red = "\u001B[31m"
        val yellow = "\u001B[33m"
        val reset = "\u001B[0m"

        return grid.rows.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                val currentCoordinates = Position(x = x, y = grid.rows.lastIndex - y)

                when (currentCoordinates) {
                    drone.currentPosition -> "${red}$cell${reset}"
                    drone.path.find { it == currentCoordinates } -> "${yellow}$cell${reset}"
                    else -> cell.toString()
                }
            }.joinToString(" ")
        }.joinToString("\n")
    }
}