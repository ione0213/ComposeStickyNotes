package com.yuchen.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.yuchen.composeapp.domain.ContextMenu
import com.yuchen.composeapp.model.YCColor

class ContextMenuViewModel(private val contextMenu: ContextMenu): ViewModel() {
    val colorOptions = contextMenu.colorOptions
    val selectedColor = contextMenu.selectedColor

    fun onDeleteClicked() {
        contextMenu.onDeleteClicked()
    }

    fun onColorSelected(color: YCColor) {
        contextMenu.onColorSelected(color)
    }

    fun onEditTextClicked() {
        contextMenu.onEditTextClicked()
    }
}