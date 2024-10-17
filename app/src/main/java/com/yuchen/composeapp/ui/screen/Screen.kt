package com.yuchen.composeapp.ui.screen

sealed class Screen(val route: String) {
    data object Editor : Screen("editor")
    data object EditText : Screen("editText/{noteId}?defaultText={defaultText}") {
        fun buildRoute(noteId: String, defaultText: String) = "editText/$noteId?$KEY_DEFAULT_TEXT=$defaultText"
        const val KEY_NOTE_ID = "noteId"
        const val KEY_DEFAULT_TEXT = "defaultText"
    }
}