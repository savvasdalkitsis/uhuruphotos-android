package com.savvasdalkitsis.uhuruphotos.foundation.image.api.video

import android.content.Context
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.math.abs
import kotlin.random.Random

internal object VideoViewCache {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .callTimeout(300, TimeUnit.SECONDS)
            .build()
    }

    fun loadInFileCached(
        url: String,
        headers: Map<String, String>?,
        context: Context,
    ): Single<String> = Single
        .create { emitter: SingleEmitter<String> ->
            var finalFile: File? = null
            var tmpFile: File? = null
            var sink: BufferedSink? = null
            var body: ResponseBody? = null

            try {
                finalFile = getOutputFile(url, context)
                if (finalFile.exists() && finalFile.length() > 0) {
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(finalFile.absolutePath)
                        return@create
                    }
                }

                finalFile.tryDelete()
                tmpFile = getTempOutputFile(url, context)

                val request: Request = buildRequest(url, headers)
                val client = VideoViewCacheFacade.customOkHttpClient ?: okHttpClient
                val response: Response = client.newCall(request).execute()

                sink = tmpFile.sink().buffer()
                body = response.body!!
                sink.writeAll(body.source())
                sink.flush()
                tmpFile.renameTo(finalFile)

                if (!emitter.isDisposed) {
                    emitter.onSuccess(finalFile.absolutePath)
                }
            } catch (e: Exception) {
                finalFile?.tryDelete()
                if (!emitter.isDisposed) {
                    emitter.tryOnError(e)
                }
            } finally {
                sink.closeSilent()
                body.closeSilent()
                tmpFile?.tryDelete()
            }
        }
        .retry(1)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    internal fun getOutputDirPath(context: Context): String =
        context.cacheDir.absolutePath + "/video_view_cache/"

    @Throws(IOException::class)
    internal fun getOutputFile(url: String, context: Context): File {
        val outputFileDir = getAndCreateOutputDirPath(context)
        val outputFileName = abs(url.hashCode()).toString() + url.split("/").last()
        return File(outputFileDir + outputFileName)
    }

    @Throws(IOException::class)
    private fun getTempOutputFile(url: String, context: Context): File {
        val outputFileDir = getAndCreateOutputDirPath(context)
        val outputFileName = "tmp_" +
                abs(url.hashCode()).toString() + url.split("/").last() +
                "${System.nanoTime()}_${abs(Random.nextLong())}"
        return File(outputFileDir + outputFileName)
    }

    @Throws(IOException::class)
    private fun getAndCreateOutputDirPath(context: Context): String {
        val outputFileDir = getOutputDirPath(context)
        val fileDir = File(outputFileDir)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        return outputFileDir
    }

    private fun buildRequest(
        url: String,
        headers: Map<String, String>?,
    ) = Request.Builder()
        .url(url)
        .also { builder ->
            headers?.entries?.forEach { (name, value) ->
                builder.addHeader(name, value)
            }
        }
        .build()
}