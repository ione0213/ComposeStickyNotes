package com.yuchen.composeapp.data

import com.yuchen.composeapp.model.StickyNote
import io.reactivex.rxjava3.core.Observable

interface NoteRepository {
    fun getAllVisibleNoteIds(): Observable<List<String>>
    fun getNoteById(id: String): Observable<StickyNote>
    fun putNote(note: StickyNote)
    fun createNote(note: StickyNote)
    fun deleteNote(noteId: String)
}