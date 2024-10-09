package com.example.composeapp.ui.view

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.model.Note
import com.example.composeapp.model.Position
import com.example.composeapp.model.YCColor
import com.example.composeapp.ui.theme.ComposeAppTheme

@Composable
fun StickyNote(
    modifier: Modifier = Modifier,
    onPositionChanged: (Position) -> Unit,
    note: Note
) {
    Surface(
        modifier
            .offset(x = note.position.x.dp, y = note.position.y.dp)
            .size(108.dp),
        color = Color(note.color.color),
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier
            .pointerInput(note.id) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onPositionChanged(Position(dragAmount.x, dragAmount.y))
                }
            }
            .padding(16.dp)) {
            Text(
                text = note.text,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StickyNotePreview() {
    ComposeAppTheme {
        StickyNote(
            onPositionChanged = {},
            note = Note(
                id = "test",
                text = "sticky note",
                Position(0F, 0F),
                YCColor(0xFFFF7EB9)
            )
        )
    }
}

