package com.savvasdalkitsis.librephotos

import android.app.Application
import android.webkit.WebView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LibrePhotosApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WebView.setWebContentsDebuggingEnabled(true)
        Timber.plant(Timber.DebugTree())
    }
}