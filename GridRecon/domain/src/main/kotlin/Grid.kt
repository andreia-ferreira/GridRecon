package net.penguin.domain

data class Grid(
    val rows: List<Row>,
    val drone: Drone
) {
    override fun toString(): String {
        val red = "\u001B[31m"
        val reset = "\u001B[0m"

        return rows.mapIndexed { y, row ->
            row.cells.mapIndexed { x, cell ->
                if (drone.matchesPosition(Position(x = x, y = rows.lastIndex - y))) {
                    "${red}*${reset}"
                } else {
                    cell.toString()
                }
            }.joinToString(" ")
        }.joinToString("\n")
    }
}