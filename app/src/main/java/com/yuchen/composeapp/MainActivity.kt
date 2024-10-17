package com.yuchen.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yuchen.composeapp.ui.screen.EditTextScreen
import com.yuchen.composeapp.ui.screen.EditorScreen
import com.yuchen.composeapp.ui.screen.Screen
import com.yuchen.composeapp.ui.theme.ComposeAppTheme
import com.yuchen.composeapp.viewmodel.EditTextViewModel
import com.yuchen.composeapp.viewmodel.EditorViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            ComposeAppTheme {
                NavHost(navController, startDestination = Screen.Editor.route) {
                    composable(Screen.Editor.route) {
                        val viewModel by viewModel<EditorViewModel>()
                        EditorScreen(viewModel) { note ->
                            navController.navigate(Screen.EditText.buildRoute(note.id, note.text))
                        }
                    }

                    composable(
                        Screen.EditText.route
                    ) { backStackEntry ->
                        val noteId =
                            backStackEntry.arguments?.getString(Screen.EditText.KEY_NOTE_ID) ?: ""
                        val defaultText =
                            backStackEntry.arguments?.getString(Screen.EditText.KEY_DEFAULT_TEXT)
                                ?: ""

                        val viewModel: EditTextViewModel = viewModel(
                            backStackEntry,
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return EditTextViewModel(
                                        getKoin().get(),
                                        noteId,
                                        defaultText
                                    ) as T
                                }
                            })

                        EditTextScreen(viewModel, onLeaveScreen = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}