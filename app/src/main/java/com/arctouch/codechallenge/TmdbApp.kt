package com.arctouch.codechallenge

import android.app.Application
import com.arctouch.codechallenge.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TmdbApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TmdbApp)
            modules(listOf(appModule))
        }
    }
}