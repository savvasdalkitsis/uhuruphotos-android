package com.savvasdalkitsis.librephotos.app

import android.app.Application
import android.webkit.WebView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.savvasdalkitsis.librephotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.image.ImageInitializer
import com.savvasdalkitsis.librephotos.log.LogInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var albumWorkScheduler: AlbumWorkScheduler
    @Inject
    lateinit var logInitializer: LogInitializer
    @Inject
    lateinit var imageInitializer: ImageInitializer

    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(true)
        logInitializer.initialize()
        imageInitializer.initialize()
        albumWorkScheduler.scheduleAlbumsRefreshPeriodic()
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}