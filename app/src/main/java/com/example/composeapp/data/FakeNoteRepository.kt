package com.example.composeapp.data

import com.example.composeapp.model.Note
import io.reactivex.rxjava3.core.Observable

class FakeNoteRepository: NoteRepository {
    private val allNotes
        get() = listOf(Note.createRandomNote(), Note.createRandomNote(), Note.createRandomNote())

    override fun getAll(): Observable<List<Note>> {
        return Observable.just(allNotes)
    }
}