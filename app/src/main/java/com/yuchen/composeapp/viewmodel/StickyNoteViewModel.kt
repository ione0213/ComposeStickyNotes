package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.domain.Editor
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.utils.fold
import io.reactivex.rxjava3.core.Observable

class StickyNoteViewModel(private val editor: Editor) : ViewModel() {

    fun moveNote(noteId: String, delta: Position) {
        editor.moveNote(noteId, delta)
    }

    fun tapNote(note: StickyNote) {
        editor.selectNote(note.id)
    }

    fun getNoteById(id: String) = editor.getNoteById(id)

    fun isSelected(id: String): Observable<Boolean> {
        return editor.selectedNote.map { optNote ->
            optNote.fold(
                someFun = { note -> note.id == id },
                emptyFun = { false }
            )
        }
    }
}