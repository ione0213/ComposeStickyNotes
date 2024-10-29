package com.yuchen.composeapp.utils

import java.util.Optional

fun <T, R> Optional<T>.fold(someFun: (T) -> R, emptyFun: () -> R): R {
    return if (this.isPresent) {
        someFun(this.get())
    } else {
        emptyFun()
    }
}