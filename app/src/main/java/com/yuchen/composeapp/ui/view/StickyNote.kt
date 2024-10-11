package com.yuchen.composeapp.ui.view

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.ui.theme.ComposeAppTheme

@Composable
fun StickyNote(
    modifier: Modifier = Modifier,
    onPositionChanged: (Position) -> Unit,
    note: Note
) {
    val offset by animateIntOffsetAsState(
        targetValue = IntOffset(
            note.position.x.toInt(),
            note.position.y.toInt()
        ),
        label = ""
    )

    // User position.dp directly without transfer with density
    // will moving too much.
    val density = LocalDensity.current
    val xDp = with(density) { note.position.x.toDp() }
    val yDp = with(density) { note.position.y.toDp() }

    Surface(
        modifier
            .offset { offset }
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

