package com.yuchen.composeapp.ui.view

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.viewmodel.StickyNoteViewModel
import org.koin.java.KoinJavaComponent.getKoin

private val highlightBorder: @Composable Modifier.(Boolean) -> Modifier = { show ->
    if (show) {
        this.border(2.dp, Color.Black, MaterialTheme.shapes.medium)
    } else {
        this
    }.padding(8.dp)
}

@Composable
fun StatefulStickyNoteView(
    modifier: Modifier = Modifier,
    noteId: String
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return
    val viewModel: StickyNoteViewModel = viewModel(
        viewModelStoreOwner,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StickyNoteViewModel(getKoin().get()) as T
            }
        }
    )
    val note by viewModel.getNoteById(noteId)
        .subscribeAsState(initial = StickyNote.createEmptyNote(noteId))
    val selected by viewModel.isSelected(noteId)
        .subscribeAsState(initial = false)

    val onPositionChanged: (Position) -> Unit = { delta ->
        viewModel.moveNote(noteId, delta)
    }

    StickyNoteView(
        modifier = modifier,
        note = note,
        selected = selected,
        onPositionChanged = onPositionChanged,
        onClick = viewModel::tapNote
    )
}

@Composable
fun StickyNoteView(
    modifier: Modifier = Modifier,
    note: StickyNote,
    selected: Boolean,
    onPositionChanged: (Position) -> Unit,
    onClick: (StickyNote) -> Unit
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
//    val density = LocalDensity.current
//    val xDp = with(density) { note.position.x.toDp() }
//    val yDp = with(density) { note.position.y.toDp() }

    Surface(
        modifier
            .offset { offset }
            .size(108.dp)
            .highlightBorder(selected),
        color = Color(note.color.color),
        shadowElevation = 8.dp,
    ) {
        Column(modifier = Modifier
            .clickable { onClick(note) }
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
//
//@Preview(showBackground = true)
//@Composable
//fun StickyNotePreview() {
//    ComposeAppTheme {
//        StickyNote(
//            onPositionChanged = {},
//            note = Note(
//                id = "test",
//                text = "sticky note",
//                Position(0F, 0F),
//                YCColor(0xFFFF7EB9)
//            )
//        )
//    }
//}

