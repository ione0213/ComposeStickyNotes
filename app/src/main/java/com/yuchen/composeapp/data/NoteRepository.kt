package com.yuchen.composeapp.data

import com.yuchen.composeapp.model.Note
import io.reactivex.rxjava3.core.Observable

interface NoteRepository {
    fun getAllNotes(): Observable<List<Note>>
    fun getNoteById(id: String): Observable<Note>
    fun putNote(note: Note)
    fun createNote(note: Note)
    fun deleteNote(noteId: String)
}