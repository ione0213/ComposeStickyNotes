package com.example.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.composeapp.data.NoteRepository
import com.example.composeapp.model.Note
import com.example.composeapp.model.Position
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.Optional

class BoardViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val disposable = CompositeDisposable()

    // Reactive programing
    val allNotes: Observable<List<Note>> = noteRepository.getAll()

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

    override fun onCleared() {
        disposable.clear()
    }
}