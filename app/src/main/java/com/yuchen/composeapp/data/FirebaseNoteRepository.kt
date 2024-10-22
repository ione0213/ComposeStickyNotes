package com.yuchen.composeapp.data

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Optional
import java.util.concurrent.TimeUnit

class FirebaseNoteRepository(firebaseFacade: FirebaseFacade) : NoteRepository {
    private val firestore = firebaseFacade.getFirestore()
    private val allNotesSubject = BehaviorSubject.createDefault(emptyList<Note>())
    private val updateNoteSubject = BehaviorSubject.createDefault(Optional.empty<Note>())

    private val query = firestore.collection(COLLECTION_NAME).limit(100)

    init {
        query.addSnapshotListener { result, _ ->
            result?.let { onSnapshotUpdated(result) }
        }

        updateNoteSubject
            .throttleLast(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { optNote ->
                optNote.ifPresent { setNoteDocument(it) }
            }

        updateNoteSubject
            .filter { it.isPresent }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                updateNoteSubject.onNext(Optional.empty<Note>())
            }
    }

    override fun getAllNotes(): Observable<List<Note>> {
        return Observables.combineLatest(updateNoteSubject, allNotesSubject)
            .map { (optNote, allNotes) ->
                optNote.map { note ->
                    val noteIndex = allNotes.indexOfFirst { it.id == note.id }
                    allNotes.subList(0, noteIndex) + note + allNotes.subList(
                        noteIndex + 1,
                        allNotes.size
                    )
                }.orElseGet { allNotes }
            }
    }

    override fun getNoteById(id: String): Observable<Note> {
        val remote = allNotesSubject.map { notes ->
            Optional.ofNullable(notes.find { note -> note.id == id })
        }.mapOptional { it }
        return updateNoteSubject.switchMap { optNote ->
            if (optNote.isPresent && optNote.get().id == id) {
                remote.map { optNote.get() }
            } else {
                remote
            }
        }
    }

    override fun putNote(note: Note) {
        updateNoteSubject.onNext(Optional.of(note))
    }

    override fun createNote(note: Note) {
        setNoteDocument(note)
    }

    override fun deleteNote(noteId: String) {
        firestore.collection(COLLECTION_NAME).document(noteId).delete()
    }

    private fun setNoteDocument(note: Note) {
        val noteData = hashMapOf(
            FIELD_COLOR to note.color.color,
            FIELD_POSITION_X to note.position.x,
            FIELD_POSITION_Y to note.position.y,
            FIELD_TEXT to note.text
        )

        firestore.collection(COLLECTION_NAME).document(note.id).set(noteData)
    }

    private fun onSnapshotUpdated(snapshot: QuerySnapshot) {
        val allNotes = snapshot.map { document ->
            documentToNote(document)
        }

        allNotesSubject.onNext(allNotes)
    }

    private fun documentToNote(document: QueryDocumentSnapshot): Note {
        val data = document.data

        val color = YCColor((data[FIELD_COLOR] as? Long) ?: 0)
        val positionX = data[FIELD_POSITION_X] as? Double ?: 0f
        val positionY = data[FIELD_POSITION_Y] as? Double ?: 0f
        val text = data[FIELD_TEXT] as? String ?: ""
        val position = Position(positionX.toFloat(), positionY.toFloat())

        return Note(document.id, text, position, color)
    }

    companion object {
        const val COLLECTION_NAME = "Notes"
        const val FIELD_COLOR = "color"
        const val FIELD_POSITION_X = "positionX"
        const val FIELD_POSITION_Y = "positionY"
        const val FIELD_TEXT = "text"
    }
}