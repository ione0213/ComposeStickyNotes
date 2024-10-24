package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.domain.Editor
import com.yuchen.composeapp.domain.usecase.MoveNoteUseCase
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.ui.state.EditorScreenState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Optional

class EditorViewModel(private val editor: Editor) : ViewModel() {
    val editorScreenState: Observable<EditorScreenState>
        get() = editor.editorScreenState
    val openEditTextScreen: Observable<Note> = editor.openEditTextScreen

    init {
        editor.start()
    }

    fun moveNote(noteId: String, delta: Position) {
        editor.moveNote(noteId, delta)
    }

    fun tapNote(note: Note) {
        editor.selectNote(note.id)
    }

    fun tapCanvas() {
        editor.clearSelection()
    }

    fun addNewNote() {
        editor.addNote()
    }

    fun deleteNote() {
        editor.contextMenu.onDeleteClicked()
    }

    fun onEditTextClicked() {
        editor.contextMenu.onEditTextClicked()
    }

    fun onColorSelected(color: YCColor) {
        editor.contextMenu.onColorSelected(color)
    }

    override fun onCleared() {
        editor.stop()
    }
}