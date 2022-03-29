package com.savvasdalkitsis.librephotos

import android.app.Application
import android.webkit.WebView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.savvasdalkitsis.librephotos.albums.worker.AlbumDownloadWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class LibrePhotosApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(true)
        Timber.plant(Timber.DebugTree())
        workManager.enqueueUniquePeriodicWork(
            AlbumDownloadWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<AlbumDownloadWorker>(2, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build(),
        )
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}