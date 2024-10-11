package com.yuchen.composeapp.data

import com.yuchen.composeapp.model.Note
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.ConcurrentHashMap

class FakeNoteRepository: NoteRepository {
    private val noteSubject = BehaviorSubject.create<List<Note>>()
    private val notesMap = ConcurrentHashMap<String, Note>()

    init {
        val initNote = Note.createRandomNote()
        notesMap[initNote.id] = initNote
        noteSubject.onNext(notesMap.elements().toList())
    }

    override fun getAllNotes(): Observable<List<Note>> {
        return noteSubject.hide()
    }

    override fun putNote(note: Note) {
        notesMap[note.id] = note
        noteSubject.onNext(notesMap.elements().toList())
    }
}