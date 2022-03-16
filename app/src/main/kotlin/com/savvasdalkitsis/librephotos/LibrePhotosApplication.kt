package com.savvasdalkitsis.librephotos

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

@HiltAndroidApp
class LibrePhotosApplication : Application() {

//    override fun onCreate() {
//        super.onCreate()
//
//        val pipelineConfig =
//            OkHttpImagePipelineConfigFactory
//                .newBuilder(this, OkHttpClient.Builder().build())
//                .setDiskCacheEnabled(true)
//                .setDownsampleEnabled(true)
//                .setResizeAndRotateEnabledForNetwork(true)
//                .build()
//
//        Fresco.initialize(this, pipelineConfig)
//    }
}