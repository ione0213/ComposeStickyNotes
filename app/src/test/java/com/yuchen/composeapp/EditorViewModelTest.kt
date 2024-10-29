package com.yuchen.composeapp

import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.domain.usecase.MoveNoteUseCase
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.viewmodel.EditorViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import java.util.Optional


internal class EditorViewModelTest {
    private val noteRepository: NoteRepository = mockk<NoteRepository>(relaxed = true)
    private val moveNoteUseCase = MoveNoteUseCase(noteRepository)
    private val viewModel = EditorViewModel(noteRepository, moveNoteUseCase)

    private fun fakeNotes(): List<StickyNote> {
        return listOf(
            StickyNote("1", "text1", Position(0f, 0f), YCColor.Aquamarine),
            StickyNote("2", "text2", Position(10f, 10f), YCColor.Gorse),
            StickyNote("3", "text3", Position(20f, 20f), YCColor.HotPink)
        )
    }

    @Test
    fun loadNotesTest() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val editorScreenStateObserver = viewModel.editorScreenState.test()
        editorScreenStateObserver.assertValue { state ->
            state.noteIds == fakeNotes()
        }
    }

    @Test
    fun `move note 1 with delta position (40, 40), expect noteRepository put Note with position (40, 40)`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        moveNoteUseCase.invoke("1", Position(40f, 40f))
//        viewModel.moveNote("1", Position(40f, 40f))

        verify {
            noteRepository.putNote(StickyNote("1", "text1", Position(40f, 40f), YCColor.Aquamarine))
        }
    }

    @Test
    fun `move note 3 with delta position (40, 40), expect noteRepository put Note with position (60, 60)`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        moveNoteUseCase.invoke("3", Position(40f, 40f))
//        viewModel.moveNote("3", Position(40f, 40f))
        verify {
            noteRepository.putNote(StickyNote("3", "text3", Position(60f, 60f), YCColor.HotPink))
        }
    }

    @Test
    fun `addNewNote, expect noteRepository add new note`() {
        every { noteRepository.getAllNotes() } returns Observable.just(emptyList())

        viewModel.addNewNote()

        verify { noteRepository.createNote(any()) }
    }

    @Test
    fun `tapNote called, expect select the tapped note`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val tappedNote = fakeNotes()[0]
        val editorScreenStateObserver = viewModel.editorScreenState.test()

        viewModel.tapNote(tappedNote)

        editorScreenStateObserver.apply {
            assertValueAt(0) { state -> state.selectedNote == Optional.empty<StickyNote>() }
            assertValueAt(1) { state -> state.selectedNote == Optional.of(tappedNote) }
        }
    }

    @Test
    fun `tapCanvas called, except no note is selected`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val tappedNote = fakeNotes()[1]
        val editorScreenStateObserver = viewModel.editorScreenState.test()

        viewModel.apply {
            tapNote(tappedNote)
            tapCanvas()
        }

        editorScreenStateObserver.assertValueAt(2) { state -> state.selectedNote == Optional.empty<StickyNote>() }
    }

    @Test
    fun `deleteNote called, expect noteRepository delete the selected note`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val noteToBeDeleted = fakeNotes()[1]

        viewModel.apply {
            tapNote(noteToBeDeleted)
            deleteNote()
        }

        verify { noteRepository.deleteNote(noteToBeDeleted.id) }
    }

    @Test
    fun `deleteNote called, expect clear the selected note`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val noteToBeDeleted = fakeNotes()[1]
        val editorScreenStateObserver = viewModel.editorScreenState.test()

        viewModel.apply {
            tapNote(noteToBeDeleted)
            deleteNote()
        }

        editorScreenStateObserver.assertValueAt(2) { state -> state.selectedNote == Optional.empty<StickyNote>() }
    }


    @Test
    fun `tapNote called, expect showing correct selecting color`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val tappedNote = fakeNotes()[0]
        val editorScreenStateObserver = viewModel.editorScreenState.test()

        viewModel.apply {
            tapNote(tappedNote)
        }

        editorScreenStateObserver.assertValueAt(1) { state -> state.selectedColor == Optional.of(tappedNote.color) }
    }

    @Test
    fun `onColorSelected called, expect noteRepository put the note with new color`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val noteToChangeColor = fakeNotes()[1]
        val newColor = YCColor.PaleCanary

        viewModel.apply {
            tapNote(noteToChangeColor)
            onColorSelected(newColor)
        }

        verify { noteRepository.putNote(noteToChangeColor.copy(color = newColor)) }
    }

    @Test
    fun `onEditTextClicked called, expect openEditTextScreen`() {
        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())

        val noteToEdit = fakeNotes()[2]
        val openEditTextScreenObserver = viewModel.openEditTextScreen.test()

        viewModel.apply {
            tapNote(noteToEdit)
            onEditTextClicked()
        }

        openEditTextScreenObserver.assertValue(noteToEdit)
    }
}