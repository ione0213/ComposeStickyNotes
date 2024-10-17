package com.yuchen.composeapp.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

fun <T : Any> Observable<T>.fromIO(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
}

fun <T : Any> Observable<T>.toIO(): Observable<T> {
    return this.observeOn(Schedulers.io())
}

fun <T : Any> Observable<T>.fromｏComputation(): Observable<T> {
    return this.subscribeOn(Schedulers.computation())
}

fun <T : Any> Observable<T>.toMain(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun <T> Maybe<T>.toIO(): Maybe<T> {
    return this.observeOn(Schedulers.io())
}