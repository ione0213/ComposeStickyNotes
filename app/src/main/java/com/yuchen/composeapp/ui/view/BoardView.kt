package com.yuchen.composeapp.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yuchen.composeapp.viewmodel.BoardViewModel


@Composable
fun BoardView(boardViewModel: BoardViewModel) {
    val notes by boardViewModel.allNotes.subscribeAsState(initial = emptyList())

    Box(Modifier.fillMaxSize()) {
        notes.forEach { note ->
            StickyNote(
                Modifier.align(Alignment.Center),
                onPositionChanged = { position ->
                    boardViewModel.moveNote(note.id, position)
                },
                note
            )
        }
    }
}