package com.yuchen.composeapp.domain

import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Optional

class Editor(private val noteRepository: NoteRepository) {
    private val selectingNoteId = BehaviorSubject.createDefault(Optional.empty<String>())
    private val _openEditTextScreenSignal = PublishSubject.create<Unit>()
    private val _showContextMenu = BehaviorSubject.createDefault(false)
    private val _showAddButton = BehaviorSubject.createDefault(true)

    val selectedNote: Observable<Optional<StickyNote>> = selectingNoteId
        .switchMap { optId ->
            if (optId.isPresent) {
                noteRepository.getNoteById(optId.get())
                    .map { Optional.ofNullable(it) }
            } else {
                Observable.just(Optional.empty())
            }
        }

    val allVisibleNoteIds: Observable<List<String>> = noteRepository.getAllVisibleNoteIds()
    val showContextMenu: Observable<Boolean> = _showContextMenu.hide()
    val showAddButton: Observable<Boolean> = _showAddButton.hide()

    val openEditTextScreen: Observable<StickyNote> = _openEditTextScreenSignal
        .withLatestFrom(selectedNote) { _, optNote ->
            optNote
        }.mapOptional { it }

    val contextMenu: ContextMenu = ContextMenu(selectedNote)

    private val disposableBag = CompositeDisposable()

    fun selectNote(noteId: String) {
        if (selectingNoteId.value?.isPresent == true && selectingNoteId.value?.get() == noteId) {
            clearSelection()
        } else {
            selectingNoteId.onNext(Optional.of(noteId))
            _showContextMenu.onNext(true)
            _showAddButton.onNext(false)
        }
    }

    fun clearSelection() {
        selectingNoteId.onNext(Optional.empty())
        _showContextMenu.onNext(false)
        _showAddButton.onNext(true)
    }

    fun getNoteById(id: String): Observable<StickyNote> {
        return noteRepository.getNoteById(id)
    }

    fun addNote() {
        val newNote = StickyNote.createRandomNote()
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
            .withLatestFrom(selectedNote) { newColor, optSelectedNote ->
                optSelectedNote.map { note ->
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