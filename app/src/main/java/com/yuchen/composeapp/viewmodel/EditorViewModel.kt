package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Optional

class EditorViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val disposable = CompositeDisposable()
    private val selectingNoteIdSubject = BehaviorSubject.createDefault("")

    // Reactive programing
    val allNotes: Observable<List<Note>> = noteRepository.getAllNotes()
    val selectingNote: Observable<Optional<Note>> =
        Observables.combineLatest(allNotes, selectingNoteIdSubject) { notes, id ->
            Optional.ofNullable(notes.find { note -> note.id == id })
        }.replay(1).autoConnect()

    val selectingColor: Observable<YCColor> = selectingNote.mapOptional { it }.map { it.color }

    fun moveNote(noteId: String, delta: Position) {
        Observable.just(Pair(noteId, delta))
            .withLatestFrom(allNotes) { (noteId, delta), notes ->
                val currentNote = notes.find { it.id == noteId }
                Optional.ofNullable(currentNote?.copy(position = currentNote.position + delta))
            }
            .mapOptional { it }
            .subscribe { newNote ->
                noteRepository.putNote(newNote)
            }.addTo(disposable)
    }

    // Imperative programing
//    private var allNotes = emptyList<Note>()
//
//    init {
//        noteRepository.getAll()
//            .subscribe { allNotes = it }
//            .addTo(disposable)
//    }
//
//    fun moveNote(id: String, position: Position) {
//        val currentNote = allNotes.find { it.id == id }
//        val newNote = currentNote?.copy(position = currentNote.position + position)
//        newNote?.let {
//            noteRepository.putNote(it)
//        }
//    }

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
        val newNote = Note.createRandomNote()
        noteRepository.createNote(newNote)
    }

    fun deleteNote() {
        runOnSelectingNote { note ->
            noteRepository.deleteNote(note.id)
            selectingNoteIdSubject.onNext("")
        }
    }

    fun onEditTextClicked() {
        TODO()
    }

    fun onColorSelected(color: YCColor) {
        runOnSelectingNote { note ->
            val newNote = note.copy(color = color)
            noteRepository.putNote(newNote)
        }
    }

    private fun runOnSelectingNote(runner: (Note) -> Unit) {
        selectingNote.take(1)
            .mapOptional { it }
            .subscribe(runner)
            .addTo(disposable)
    }

    override fun onCleared() {
        disposable.clear()
    }
}