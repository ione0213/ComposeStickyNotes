package com.yuchen.composeapp.di

import com.yuchen.composeapp.data.FakeNoteRepository
import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.viewmodel.BoardViewModel
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