package com.example.composeapp.di

import com.example.composeapp.data.FakeNoteRepository
import com.example.composeapp.data.NoteRepository
import com.example.composeapp.viewmodel.BoardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun getModule() = module {
    viewModel {
        BoardViewModel(get())
    }

    single<NoteRepository> {
        FakeNoteRepository()
    }
}