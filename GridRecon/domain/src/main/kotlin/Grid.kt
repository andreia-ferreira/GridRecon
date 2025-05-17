package net.penguin.domain

data class Grid(
    val rows: List<List<Int>>

) {
    override fun toString(): String {
        return rows.joinToString("\n") {
            it.joinToString(" ")
        }
    }
}