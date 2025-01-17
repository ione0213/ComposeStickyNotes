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
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.ui.theme.ComposeAppTheme
import com.yuchen.composeapp.ui.view.StatefulContextMenuView
import com.yuchen.composeapp.ui.view.ViewPort
import com.yuchen.composeapp.utils.subscribeBy
import com.yuchen.composeapp.utils.toMain
import com.yuchen.composeapp.viewmodel.EditorViewModel
import org.koin.java.KoinJavaComponent.getKoin

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    openEditTextScreen: (StickyNote) -> Unit
) {
    viewModel.openEditTextScreen
        .toMain()
        .subscribeBy(onNext = openEditTextScreen)

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput("Editor") {
                    detectTapGestures { viewModel.tapCanvas() }
                }
        ) {
            val showAddButton by viewModel.showAddButton.subscribeAsState(true)
            val showContextMenu by viewModel.showContextMenu.subscribeAsState(false)
            val noteIds by viewModel.allVisibleNoteIds.subscribeAsState(emptyList())

            ViewPort(noteIds)

            AnimatedVisibility(
                visible = showAddButton,
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
                visible = showContextMenu,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                StatefulContextMenuView()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    ComposeAppTheme {
        EditorScreen(EditorViewModel(getKoin().get())) {}
    }
}