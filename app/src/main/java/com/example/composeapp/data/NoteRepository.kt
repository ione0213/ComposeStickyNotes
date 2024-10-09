package com.example.composeapp.data

import com.example.composeapp.model.Note
import io.reactivex.rxjava3.core.Observable

interface NoteRepository {
    fun getAll(): Observable<List<Note>>

    fun putNote(note: Note)
}