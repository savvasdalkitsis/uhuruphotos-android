package com.savvasdalkitsis.uhuruphotos.app

import android.app.Application
import android.webkit.WebView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var applicationInitializer: ApplicationInitializer

    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(true)
        applicationInitializer.onCreated()
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}