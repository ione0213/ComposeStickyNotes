package com.yuchen.composeapp.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.reactivex.rxjava3.core.Observable

@Composable
fun <T : Any> Observable<T>.subscribeBy(
    onNext: (T) -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
) {
    DisposableEffect(this) {
        val disposable = subscribe(onNext, onError, onComplete)
        onDispose { disposable.dispose() }
    }
}
