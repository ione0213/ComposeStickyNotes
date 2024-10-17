package com.yuchen.composeapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.yuchen.composeapp.R
import com.yuchen.composeapp.data.FakeNoteRepository
import com.yuchen.composeapp.ui.theme.ComposeAppTheme
import com.yuchen.composeapp.ui.theme.TransparentBlack
import com.yuchen.composeapp.utils.subscribeBy
import com.yuchen.composeapp.utils.toMain
import com.yuchen.composeapp.viewmodel.EditTextViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTextScreen(
    editTextViewModel: EditTextViewModel,
    onLeaveScreen: () -> Unit
) {
    val text by editTextViewModel.text.subscribeAsState(initial = "")

    editTextViewModel.leavePage
        .toMain()
        .subscribeBy(onNext = { onLeaveScreen() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .background(TransparentBlack)
    ) {
        TextField(
            value = text,
            onValueChange = editTextViewModel::onTextChanged,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(fraction = 0.8f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White
            ),
            textStyle = MaterialTheme.typography.headlineSmall
        )

        IconButton(
            modifier = Modifier.align(Alignment.TopStart),
            onClick = editTextViewModel::onCancelClicked
        ) {
            val painter = painterResource(id = R.drawable.ic_close)
            Icon(
                painter = painter,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = editTextViewModel::onConfirmClicked
        ) {
            val painter = painterResource(id = R.drawable.ic_chek)
            Icon(
                painter = painter,
                contentDescription = "Check",
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTextScreenPreview() {
    ComposeAppTheme {
        EditTextScreen(
            EditTextViewModel(FakeNoteRepository(), "", "")
        ) {}
    }
}