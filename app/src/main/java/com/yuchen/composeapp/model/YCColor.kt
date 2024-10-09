package com.yuchen.composeapp.model

data class YCColor(
    val color: Long
) {
    companion object {
        val HotPink = YCColor(0xFFFF7EB9)
        val Aquamarine = YCColor(0xFF7AFCFF)
        val PaleCanary = YCColor(0xFFFEFF9C)
        val Gorse = YCColor(0xFFFFF740)

        val defaultColors = listOf(HotPink, Aquamarine, PaleCanary, Gorse)
    }
}
