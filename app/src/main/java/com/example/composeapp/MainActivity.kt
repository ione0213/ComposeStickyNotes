package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.view.BoardView
import com.example.composeapp.viewmodel.BoardViewModel
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