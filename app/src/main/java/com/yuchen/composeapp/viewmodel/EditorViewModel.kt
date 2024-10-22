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

class EditorViewModel(
    private val noteRepository: NoteRepository,
    private val moveNoteUseCase: MoveNoteUseCase,
    private val editor: Editor
) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val selectingNoteIdSubject = BehaviorSubject.createDefault("")
    private val openEditTextSubject = PublishSubject.create<Note>()

    val editorScreenState: Observable<EditorScreenState>
        get() = Observables.combineLatest(noteRepository.getAllNotes(), selectingNoteIdSubject) { notes, id ->
            val selectedNote = Optional.ofNullable(notes.find { note -> note.id == id })
            EditorScreenState(notes.toMutableList(), selectedNote)
        }.replay(1).autoConnect()
    val openEditTextScreen: Observable<Note> = openEditTextSubject.hide()

    init {
        editor.start()
    }

    fun moveNote(noteId: String, delta: Position) {
//        editorScreenState.take(1)
//            .map { screenState ->
//                val currentNote = screenState.notes.find { it.id == noteId }
//                Optional.ofNullable(currentNote?.copy(position = currentNote.position + delta))
//            }
//            .mapOptional { it }
//            .subscribe { newNote ->
//                noteRepository.putNote(newNote)
//            }
//            .addTo(disposable)
//        moveNoteUseCase(noteId, delta).addTo(disposable)
        editor.moveNote(noteId, delta)
    }

    fun tapNote(note: Note) {
        val selectingNoteId = selectingNoteIdSubject.value
        if (selectingNoteId == note.id) {
            selectingNoteIdSubject.onNext("")
        } else {
            selectingNoteIdSubject.onNext(note.id)
        }
    }

    fun tapCanvas() {
        selectingNoteIdSubject.onNext("")
    }

    fun addNewNote() {
        editor.addNote()
    }

    fun deleteNote() {
        runOnSelectingNote { note ->
            noteRepository.deleteNote(note.id)
            selectingNoteIdSubject.onNext("")
        }
    }

    fun onEditTextClicked() {
        runOnSelectingNote { note ->
            openEditTextSubject.onNext(note)
        }
    }

    fun onColorSelected(color: YCColor) {
        runOnSelectingNote { note ->
            val newNote = note.copy(color = color)
            noteRepository.putNote(newNote)
        }
    }

    private fun runOnSelectingNote(runner: (Note) -> Unit) {
        editorScreenState.take(1)
            .map { it.selectedNote }
            .mapOptional { it }
            .subscribe(runner)
            .addTo(disposable)
    }

    override fun onCleared() {
        disposable.clear()
        editor.stop()
    }
}