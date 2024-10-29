package com.yuchen.composeapp.domain

import com.yuchen.composeapp.model.StickyNote
import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Optional

class ContextMenu(
    private val selectedNote: Observable<Optional<StickyNote>>
) {
    val colorOptions: List<YCColor> = YCColor.defaultColors
    val selectedColor: Observable<YCColor> = selectedNote.mapOptional { it }.map { it.color }

    private val _contextMenuEvent = PublishSubject.create<ContextMenuEvent>()
    val contextMenuEvent: Observable<ContextMenuEvent> = _contextMenuEvent.hide()

    fun onColorSelected(color: YCColor) {
        _contextMenuEvent.onNext(ContextMenuEvent.ChangeColor(color))
    }

    fun onDeleteClicked() {
        _contextMenuEvent.onNext(ContextMenuEvent.DeleteNote)
    }

    fun onEditTextClicked() {
        _contextMenuEvent.onNext(ContextMenuEvent.NavigateToEditTextPage)
    }
}

sealed interface ContextMenuEvent {
    data object NavigateToEditTextPage : ContextMenuEvent
    data object DeleteNote : ContextMenuEvent
    class ChangeColor(val color: YCColor) : ContextMenuEvent
}