package com.yuchen.composeapp.data

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.yuchen.composeapp.model.Note
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class FirebaseNoteRepository(firebaseFacade: FirebaseFacade) : NoteRepository {
    private val firestore = firebaseFacade.getFirestore()
    private val notesSubject = BehaviorSubject.createDefault(emptyList<Note>())

    private val query = firestore.collection(COLLECTION_NAME).limit(100)

    init {
        query.addSnapshotListener { result, _ ->
            result?.let { onSnapshotUpdated(result) }
        }
    }

    override fun getAll(): Observable<List<Note>> = notesSubject.hide()


    override fun putNote(note: Note) {
        setNoteDocument(note)
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

        notesSubject.onNext(allNotes)
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