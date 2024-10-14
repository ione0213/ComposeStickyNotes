package com.yuchen.composeapp.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuchen.composeapp.R
import com.yuchen.composeapp.data.FakeNoteRepository
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.ui.theme.ComposeAppTheme
import com.yuchen.composeapp.ui.view.BoardView
import com.yuchen.composeapp.ui.view.MenuView
import com.yuchen.composeapp.viewmodel.EditorViewModel
import java.util.Optional

@Composable
fun EditorScreen(viewModel: EditorViewModel) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput("Editor") {
                    detectTapGestures { viewModel.tapCanvas() }
                }
        ) {
            val selectedNote by viewModel.selectingNote.subscribeAsState(initial = Optional.empty())
            val selectingColor by viewModel.selectingColor.subscribeAsState(initial = YCColor.Aquamarine)
            val allNotes by viewModel.allNotes.subscribeAsState(initial = emptyList())

            BoardView(
                allNotes,
                selectedNote,
                viewModel::moveNote,
                viewModel::tapNote
            )

            AnimatedVisibility(
                visible = !selectedNote.isPresent,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    onClick = viewModel::addNewNote,
                    modifier = Modifier.padding(8.dp),
                    shape = CircleShape
                ) {
                    val painter = painterResource(R.drawable.ic_add)
                    Icon(painter, "Add")
                }
            }

            AnimatedVisibility(
                visible = selectedNote.isPresent,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                MenuView(
                    selectedColor = selectingColor,
                    onDeleteClicked = viewModel::deleteNote,
                    onTextClicked = viewModel::onEditTextClicked,
                    onColorSelected = viewModel::onColorSelected
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    ComposeAppTheme {
        EditorScreen(EditorViewModel(FakeNoteRepository()))
    }
}