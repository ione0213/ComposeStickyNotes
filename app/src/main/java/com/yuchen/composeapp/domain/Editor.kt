package com.yuchen.composeapp.domain

import com.yuchen.composeapp.data.NoteRepository
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

class Editor(private val noteRepository: NoteRepository) {
    //    val allVisibleNotes: Observable<List<String>>
//    val selectedNote: Observable<Optional<Note>>
    private val selectingNoteId = BehaviorSubject.createDefault(Optional.empty<String>())
    private val _openEditTextScreenSignal = PublishSubject.create<Unit>()

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

    val openEditTextScreen: Observable<Note> = _openEditTextScreenSignal
        .withLatestFrom(editorScreenState) { _, state -> state.selectedNote }
        .mapOptional { it }

    val contextMenu: ContextMenu = ContextMenu(
        editorScreenState.map { state ->
            if (state.selectedNote.isPresent) {
                Optional.ofNullable(state.selectedNote.get())
            } else {
                Optional.empty()
            }
        }
    )

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

    private fun navigateToEditTextPage() {
        _openEditTextScreenSignal.onNext(Unit)
    }

    private fun changeColor(color: YCColor) {
        Observable.just(color)
            .withLatestFrom(editorScreenState) { newColor, state ->
                state.selectedNote.map { note ->
                    note.copy(color = newColor)
                }
            }.mapOptional { it }
            .subscribe { newNote ->
                noteRepository.putNote(newNote)
            }
            .addTo(disposableBag)
    }

    private fun deleteNote() {
        selectingNoteId.value?.ifPresent { id ->
            noteRepository.deleteNote(id)
            clearSelection()
        }
    }

    fun start() {
        contextMenu.contextMenuEvent
            .subscribe { menuEvent ->
                when (menuEvent) {
                    ContextMenuEvent.NavigateToEditTextPage -> navigateToEditTextPage()
                    is ContextMenuEvent.ChangeColor -> changeColor(menuEvent.color)
                    ContextMenuEvent.DeleteNote -> deleteNote()
                }
            }.addTo(disposableBag)
    }

    fun stop() {
        disposableBag.clear()
    }
}