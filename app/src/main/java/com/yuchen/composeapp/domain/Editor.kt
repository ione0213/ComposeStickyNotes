package com.yuchen.composeapp.domain

import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.ui.state.EditorScreenState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Optional

class Editor(private val noteRepository: NoteRepository) {
    //    val allVisibleNotes: Observable<List<String>>
//    val selectedNote: Observable<Optional<Note>>
    private val selectingNoteId = BehaviorSubject.createDefault(Optional.empty<String>())
    val editorScreenState: Observable<EditorScreenState>
        get() = Observables.combineLatest(
            noteRepository.getAllNotes(),
            selectingNoteId
        ) { notes, optionalId ->
            val selectedNote = optionalId.flatMap { id ->
                Optional.ofNullable(notes.find { note -> note.id == id })
            }
            EditorScreenState(notes.toMutableList(), selectedNote)
        }.replay(1).autoConnect()


    val showContextMenu: Observable<Boolean> = BehaviorSubject.createDefault(false)
    val showAdderButton: Observable<Boolean> = BehaviorSubject.createDefault(false)

//    val openEditTextScreen: Observable<String> = TODO()

    val contextMenu: ContextMenu = ContextMenu()

    private val disposableBag = CompositeDisposable()

    fun selectNote(noteId: String) {
        if (selectingNoteId.value?.isPresent == true && selectingNoteId.value?.get() == noteId) {
            clearSelection()
        } else {
            selectingNoteId.onNext(Optional.of(noteId))
        }
    }

    fun clearSelection() {
        selectingNoteId.onNext(Optional.empty())
    }

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