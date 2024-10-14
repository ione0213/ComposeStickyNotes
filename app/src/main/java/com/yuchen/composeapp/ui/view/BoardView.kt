package com.yuchen.composeapp.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.viewmodel.EditorViewModel
import java.util.Optional


@Composable
fun BoardView(
    notes: List<Note>,
    selectedNote: Optional<Note>,
    updatePosition: (String, Position) -> Unit,
    onNoteClicked: (Note) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        notes.forEach { note ->
            val onPositionCHanged: (Position) -> Unit = {
                updatePosition(note.id, it)
            }

            val isSelected = selectedNote.filter { it.id == note.id }.isPresent

            StickyNote(
                modifier = Modifier.align(Alignment.Center),
                note = note,
                selected = isSelected,
                onPositionChanged = onPositionCHanged,
                onClick = onNoteClicked
            )
        }
    }
}