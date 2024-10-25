package com.yuchen.composeapp.di

import com.yuchen.composeapp.data.FirebaseFacade
import com.yuchen.composeapp.data.FirebaseNoteRepository
import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.domain.Editor
import com.yuchen.composeapp.domain.usecase.MoveNoteUseCase
import com.yuchen.composeapp.viewmodel.EditorViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun getModule() =
    module {
        viewModel {
            EditorViewModel(
                editor = get()
            )
        }

//        viewModel {
//            ContextMenuViewModel(
//                contextMenu = get<Editor>().contextMenu
//            )
//        }
//
//        viewModel { (noteId: String, defaultText: String) ->
//            EditTextViewModel(
//                noteRepository = get(),
//                noteId = noteId,
//                defaultText = defaultText
//            )
//        }

        single<NoteRepository> {
            FirebaseNoteRepository(get())
        }

        single { FirebaseFacade() }

        single {
            Editor(noteRepository = get())
        }

        single {
            MoveNoteUseCase(get())
        }

        single {
            get<Editor>().contextMenu
        }
    }