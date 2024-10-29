package com.yuchen.composeapp.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun ViewPort(noteIds: List<String>) {
    Box(Modifier.fillMaxSize()) {
        noteIds.forEach { id ->
            key(id) {
                StatefulStickyNoteView(
                    modifier = Modifier.align(Alignment.Center),
                    noteId = id
                )
            }
        }
    }
}