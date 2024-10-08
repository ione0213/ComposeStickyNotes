package com.example.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.composeapp.data.NoteRepository
import com.example.composeapp.model.Note
import io.reactivex.rxjava3.core.Observable

class BoardViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    val allNotes: Observable<List<Note>> = noteRepository.getAll()
}