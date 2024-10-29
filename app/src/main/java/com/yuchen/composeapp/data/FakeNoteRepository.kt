package com.yuchen.composeapp.data

import com.yuchen.composeapp.model.StickyNote
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.ConcurrentHashMap

class FakeNoteRepository: NoteRepository {
    private val noteSubject = BehaviorSubject.create<List<StickyNote>>()
    private val notesMap = ConcurrentHashMap<String, StickyNote>()

    init {
        val initNote = StickyNote.createRandomNote()
        notesMap[initNote.id] = initNote
        noteSubject.onNext(notesMap.elements().toList())
    }

    override fun getAllVisibleNoteIds(): Observable<List<String>> {
        TODO("Not yet implemented")
    }

    override fun getNoteById(id: String): Observable<StickyNote> {
        TODO()
    }

    override fun putNote(note: StickyNote) {
        notesMap[note.id] = note
        noteSubject.onNext(notesMap.elements().toList())
    }

    override fun createNote(note: StickyNote) {
        TODO()
    }

    override fun deleteNote(noteId: String) {
        TODO()
    }
}