package com.yuchen.composeapp.model

import java.util.UUID
import kotlin.random.Random

data class Note(
    val id: String,
    val text: String,
    val position: Position,
    val color: YCColor
) {
    companion object {
        fun createRandomNote(): Note {
            val randomId = UUID.randomUUID().toString()
            val randomPosition = Position(Random.nextInt(-50, 50).toFloat(), Random.nextInt(-50, 50).toFloat())
            val randomColorIndex = YCColor.defaultColors[(Random.nextInt(0, YCColor.defaultColors.size))]

            return Note(randomId, "New Note", randomPosition, randomColorIndex)
        }
    }
}
