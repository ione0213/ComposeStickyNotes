package com.yuchen.composeapp.domain.usecase


import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.Position
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.Optional

class MoveNoteUseCase(private val noteRepository: NoteRepository) {
    operator fun invoke(noteId: String, deltaPosition: Position): Disposable {
        return Observable.just(Pair(noteId, deltaPosition))
            .withLatestFrom(noteRepository.getAllNotes()) { (id, delta), notes ->
                val currentNote = notes.find { it.id == id }
                Optional.ofNullable(currentNote?.copy(position = currentNote.position + delta))
            }.mapOptional { it }
            .subscribe { note ->
                noteRepository.putNote(note)
            }
    }
}