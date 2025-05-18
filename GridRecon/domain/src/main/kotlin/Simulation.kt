package net.penguin.domain

class Simulation(
    private val maxSteps: Int,
    private val maxDuration: Long,
    private val grid: Grid,
    private val drone: Drone
) {


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