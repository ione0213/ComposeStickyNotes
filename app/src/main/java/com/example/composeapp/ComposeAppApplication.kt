package com.example.composeapp

import android.app.Application
import com.example.composeapp.di.getModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ComposeAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ComposeAppApplication)
            modules(getModule())
        }
    }
}