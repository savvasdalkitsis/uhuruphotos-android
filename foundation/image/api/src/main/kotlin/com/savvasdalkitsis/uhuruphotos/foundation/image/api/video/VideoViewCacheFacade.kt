package com.savvasdalkitsis.uhuruphotos.foundation.image.api.video

import android.content.Context
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException

object VideoViewCacheFacade {

    var customOkHttpClient: OkHttpClient? = null

    @Throws(IOException::class)
    fun cleanCacheFor(url: String, context: Context): Boolean =
        VideoViewCache.getOutputFile(url, context).tryDelete()

    @Throws(IOException::class)
    fun getOutputFile(url: String, context: Context): File = VideoViewCache.getOutputFile(url, context)

    fun getOutputDirPath(context: Context): String = VideoViewCache.getOutputDirPath(context)
}