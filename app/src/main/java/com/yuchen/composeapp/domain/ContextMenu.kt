package com.yuchen.composeapp.domain

import com.yuchen.composeapp.model.YCColor
import io.reactivex.rxjava3.core.Observable

class ContextMenu {
//    val colorOptions: List<String> = TODO()
//    val selectedColor: Observable<YCColor> = TODO()

    fun onColorSelected(color: YCColor) {}

    fun onDeleteClicked() {}

    fun onEditTextClicked() {}
}