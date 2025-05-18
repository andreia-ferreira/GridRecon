package net.penguin.domain.entity

enum class Direction(val vector: Pair<Int, Int>) {
    UP(0 to -1),
    UP_RIGHT(1 to -1),
    RIGHT(1 to 0),
    DOWN_RIGHT(1 to 1),
    DOWN(0 to 1),
    DOWN_LEFT(-1 to 1),
    LEFT(-1 to 0),
    UP_LEFT(-1 to -1),
}