package com.savvasdalkitsis.librephotos

import android.app.Application
import android.webkit.WebView
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class LibrePhotosApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(true)
        Timber.plant(Timber.DebugTree())
    }
}