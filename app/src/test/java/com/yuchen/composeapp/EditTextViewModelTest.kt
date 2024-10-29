package com.yuchen.composeapp

import com.yuchen.composeapp.data.NoteRepository
import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.Position
import com.yuchen.composeapp.model.YCColor
import com.yuchen.composeapp.viewmodel.EditTextViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.Test

class EditTextViewModelTest {
    private val noteRepository: NoteRepository = mockk<NoteRepository>(relaxed = true)
    private val noteId = "1"
    private val defaultText = "text1"
    private val viewModel = EditTextViewModel(noteRepository, noteId, defaultText)

    private fun fakeNotes(): List<StickyNote> {
        return listOf(
            StickyNote("1", "text1", Position(0f, 0f), YCColor.Aquamarine),
            StickyNote("2", "text2", Position(10f, 10f), YCColor.Gorse),
            StickyNote("3", "text3", Position(20f, 20f), YCColor.HotPink)
        )
    }

    @Test
    fun `load correct text from start`() {
        val textObserver = viewModel.text.test()

        textObserver.assertValue(defaultText)
    }

    @Test
    fun `onTextChanged called, expect display the correct text`() {
        val textObserver = viewModel.text.test()
        val newText = "text1 changed"

        viewModel.onTextChanged(newText)

        textObserver.assertValueAt(1, newText)
    }

    @Test
    fun `onConfirmClicked called, expect update note with new text`() {
        val note =
            fakeNotes().find { it.id == noteId } ?: throw IllegalStateException("Note not found")
        val newText = "text1 changed"

        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())
        every { noteRepository.getNoteById(noteId) } returns Observable.just(note)

        viewModel.onTextChanged(newText)
        viewModel.onConfirmClicked()

        verify { noteRepository.putNote(note.copy(text = newText)) }
    }

    @Test
    fun `onConfirmClicked called, expect leave page`() {
        val note =
            fakeNotes().find { it.id == noteId } ?: throw IllegalStateException("Note not found")
        val newText = "text1 changed"
        val leavePageObserver = viewModel.leavePage.test()

        every { noteRepository.getAllNotes() } returns Observable.just(fakeNotes())
        every { noteRepository.getNoteById(noteId) } returns Observable.just(note)

        viewModel.onTextChanged(newText)
        viewModel.onConfirmClicked()

        leavePageObserver.assertValue(Unit)
    }

    @Test
    fun `onCancelClicked called, expect leave page`() {
        val leavePageObserver = viewModel.leavePage.test()

        viewModel.onCancelClicked()

        leavePageObserver.assertValue(Unit)
    }
}