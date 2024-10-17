package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.data.NoteRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class EditTextViewModel(
    private val noteRepository: NoteRepository,
    private val noteId: String,
    defaultText: String
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private val textSubject = BehaviorSubject.createDefault(defaultText)
    val text: Observable<String> = textSubject.hide()

    private val leavePageSubject = PublishSubject.create<Unit>()
    val leavePage: Observable<Unit> = leavePageSubject.hide()

    fun onTextChanged(newText: String) {
        textSubject.onNext(newText)
    }

    fun onConfirmClicked() {
        noteRepository.getNoteById(noteId)
            .withLatestFrom(text) { note, text ->
                note.copy(text = text)
            }.firstElement()
            .subscribe { newNote ->
                noteRepository.putNote(newNote)
                leavePageSubject.onNext(Unit)
            }
            .addTo(disposable)
    }

    fun onCancelClicked() {
        leavePageSubject.onNext(Unit)
    }

    override fun onCleared() {
        disposable.clear()
    }
}