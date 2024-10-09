package com.example.composeapp.model

data class Position(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: Position): Position {
        return Position(x + other.x, y + other.y)
    }
}
