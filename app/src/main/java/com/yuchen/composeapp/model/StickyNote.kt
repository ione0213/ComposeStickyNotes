package com.yuchen.composeapp.model

import java.util.UUID
import kotlin.random.Random

data class StickyNote(
    val id: String,
    val text: String,
    val position: Position,
    val color: YCColor
) {
    companion object {
        fun createRandomNote(): StickyNote {
            val randomId = UUID.randomUUID().toString()
            val randomPosition = Position(Random.nextInt(-50, 50).toFloat(), Random.nextInt(-50, 50).toFloat())
            val randomColorIndex = YCColor.defaultColors[(Random.nextInt(0, YCColor.defaultColors.size))]

            return StickyNote(randomId, "New Note", randomPosition, randomColorIndex)
        }

        fun createEmptyNote(id: String): StickyNote {
            return StickyNote(id, "", Position(0f, Float.MAX_VALUE), YCColor.HotPink)
        }
    }
}
