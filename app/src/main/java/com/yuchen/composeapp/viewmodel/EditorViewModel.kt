package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.domain.Editor
import com.yuchen.composeapp.model.StickyNote
import io.reactivex.rxjava3.core.Observable

class EditorViewModel(private val editor: Editor) : ViewModel() {
    val openEditTextScreen: Observable<StickyNote> = editor.openEditTextScreen
    val allVisibleNoteIds = editor.allVisibleNoteIds
    val showContextMenu = editor.showContextMenu
    val showAddButton = editor.showAddButton

    init {
        editor.start()
    }

    fun tapCanvas() {
        editor.clearSelection()
    }

    fun addNewNote() {
        editor.addNote()
    }

    override fun onCleared() {
        editor.stop()
    }
}