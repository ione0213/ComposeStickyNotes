package com.yuchen.composeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.yuchen.composeapp.ui.theme.ComposeAppTheme
import com.yuchen.composeapp.ui.view.BoardView
import com.yuchen.composeapp.viewmodel.BoardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAppTheme {
                val viewModel by viewModel<BoardViewModel>()
                BoardView(viewModel)
            }
        }
    }
}