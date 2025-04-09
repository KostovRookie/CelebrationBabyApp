package com.stanga.nanit.application

import android.app.Application
import com.stanga.nanit.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NanitApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NanitApp)
            modules(appModule)
        }
    }
}