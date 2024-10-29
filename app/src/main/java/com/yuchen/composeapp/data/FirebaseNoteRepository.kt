package com.yuchen.composeapp.data

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional
import java.util.concurrent.TimeUnit

class FirebaseNoteRepository(firebaseFacade: FirebaseFacade) : NoteRepository {
    private val firestore = firebaseFacade.getFirestore()
    private val allNoteIdsSubject = BehaviorSubject.create<List<String>>()
    private val updatingNoteSubject = BehaviorSubject.createDefault(Optional.empty<StickyNote>())

    private val registrations = hashMapOf<String, ListenerRegistration>()
    private val noteSubjects = hashMapOf<String, Subject<StickyNote>>()

    private val query = firestore.collection(COLLECTION_NAME).limit(100)

    init {
        query.addSnapshotListener { result, _ ->
            result?.let { onSnapshotUpdated(result) }
        }

        updatingNoteSubject
            .throttleLast(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { optNote ->
                optNote.ifPresent { setNoteDocument(it) }
            }

        updatingNoteSubject
            .filter { it.isPresent }
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                updatingNoteSubject.onNext(Optional.empty<StickyNote>())
            }
    }

    override fun getAllVisibleNoteIds(): Observable<List<String>> {
        return allNoteIdsSubject.hide()
    }

    override fun getNoteById(id: String): Observable<StickyNote> {
        noteSubjects[id]?.let {
            return combineNoteSignals(it, id)
        }

        val documentRef = createDocumentRef(id)
        val noteSubject = BehaviorSubject.create<StickyNote>()
        val registration = documentRef.addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                val note = documentToNote(snapshot) ?: return@addSnapshotListener
                noteSubject.onNext(note)
            }
        }

        registrations[id] = registration
        noteSubjects[id] = noteSubject

        return combineNoteSignals(noteSubject, id)
    }

    private fun combineNoteSignals(remote: Observable<StickyNote>, id: String): Observable<StickyNote> {
        return updatingNoteSubject.switchMap { optNote ->
            if (optNote.isPresent && optNote.get().id == id) {
                remote.map { optNote.get() }
            } else {
                remote
            }
        }
    }

    override fun putNote(note: StickyNote) {
        updatingNoteSubject.onNext(Optional.of(note))
    }

    override fun createNote(note: StickyNote) {
        setNoteDocument(note)
    }

    override fun deleteNote(noteId: String) {
        registrations[noteId]?.remove()
        noteSubjects[noteId]?.onComplete()

        firestore.collection(COLLECTION_NAME).document(noteId).delete()
    }

    private fun setNoteDocument(note: StickyNote) {
        val noteData = hashMapOf(
            FIELD_COLOR to note.color.color,
            FIELD_POSITION_X to note.position.x,
            FIELD_POSITION_Y to note.position.y,
            FIELD_TEXT to note.text
        )

        firestore.collection(COLLECTION_NAME).document(note.id).set(noteData)
    }

    private fun onSnapshotUpdated(snapshot: QuerySnapshot) {
        if (snapshot.documentChanges.any { it.type == DocumentChange.Type.ADDED || it.type == DocumentChange.Type.REMOVED }) {
            val allNoteIds = snapshot.map { it.id }
            allNoteIdsSubject.onNext(allNoteIds)
        }

        snapshot.documentChanges
            .filter { it.type == DocumentChange.Type.REMOVED }
            .forEach { documentChange ->
                val id = documentChange.document.id
                registrations[id]?.remove()
                noteSubjects[id]?.onComplete()
            }
    }

    private fun createDocumentRef(id: String): DocumentReference {
        return firestore.collection(COLLECTION_NAME).document(id)
    }

    private fun documentToNote(document: DocumentSnapshot): StickyNote? {
        val data = document.data ?: return null

        val color = YCColor((data[FIELD_COLOR] as? Long) ?: 0)
        val positionX = data[FIELD_POSITION_X] as? Double ?: 0f
        val positionY = data[FIELD_POSITION_Y] as? Double ?: 0f
        val text = data[FIELD_TEXT] as? String ?: ""
        val position = Position(positionX.toFloat(), positionY.toFloat())

        return StickyNote(document.id, text, position, color)
    }

    companion object {
        const val COLLECTION_NAME = "Notes"
        const val FIELD_COLOR = "color"
        const val FIELD_POSITION_X = "positionX"
        const val FIELD_POSITION_Y = "positionY"
        const val FIELD_TEXT = "text"
    }
}