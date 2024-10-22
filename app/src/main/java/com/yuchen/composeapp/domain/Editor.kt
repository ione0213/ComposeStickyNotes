package com.yuchen.composeapp.domain

import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Optional

class Editor(private val noteRepository: NoteRepository) {
//    val allVisibleNotes: Observable<List<String>>
//    val selectedNote: Observable<Optional<Note>>

    val showContextMenu: Observable<Boolean> = BehaviorSubject.createDefault(false)
    val showAdderButton: Observable<Boolean> = BehaviorSubject.createDefault(false)

//    val openEditTextScreen: Observable<String> = TODO()

    val contextMenu: ContextMenu = ContextMenu()

    private val disposableBag = CompositeDisposable()

    fun selectNote(noteId: String) {}

    fun clearSelection() {}

    fun addNote() {
        val newNote = Note.createRandomNote()
        noteRepository.createNote(newNote)
    }

    fun moveNote(noteId: String, positionDelta: Position) {
        Observable.just(positionDelta)
            .withLatestFrom(noteRepository.getNoteById(noteId)) { delta, note ->
                note.copy(position = note.position + delta)
            }
            .subscribe { note ->
                noteRepository.putNote(note)
            }
            .addTo(disposableBag)
    }

    fun start() {}

    fun stop() {
        disposableBag.clear()
    }
}