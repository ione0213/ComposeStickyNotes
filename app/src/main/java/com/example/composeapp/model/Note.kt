package com.example.composeapp.model

data class Note(
    val id: String,
    val text: String,
    val position: Position,
    val color: YCColor
)
