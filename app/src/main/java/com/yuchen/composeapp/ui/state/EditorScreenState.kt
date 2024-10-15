package com.yuchen.composeapp.ui.state

import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.YCColor
import java.util.Optional

data class EditorScreenState(
    val notes: List<Note>,
    val selectedNote: Optional<Note>
) {
    val selectedColor: Optional<YCColor> = selectedNote.map { it.color }
    val showAddButton = !selectedNote.isPresent
    val showMenu = selectedNote.isPresent
}
