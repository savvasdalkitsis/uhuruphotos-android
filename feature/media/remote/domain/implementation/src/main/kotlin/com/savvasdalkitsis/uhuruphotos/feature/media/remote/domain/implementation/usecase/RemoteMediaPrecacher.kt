/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.usecase

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.SuccessResult
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.coroutines.resume

@AutoBind
class RemoteMediaPrecacher @Inject constructor(
    @ApplicationContext val context: Context,
    private val imageLoader: ImageLoader,
) : RemoteMediaPrecacher {

    override suspend fun precacheMedia(
        url: String,
        video: Boolean,
    ): Boolean = try {
        if (video) {
            cacheVideoThumbnail(url)
        } else {
            cachePhotoThumbnail(url)
        }
    } catch (e: Exception) {
        log(e)
        false
    }

    private suspend fun cacheVideoThumbnail(url: String): Boolean {
        val result = imageLoader.execute(
            coil.request.ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .target()
                .build()
        )
        if (result is ErrorResult) {
            log(result.throwable) { "Error precaching video thumbnail: $url"}
        }
        return result is SuccessResult
    }

    private suspend fun cachePhotoThumbnail(url: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            Fresco.getImagePipeline()
                .prefetchToDiskCache(
                    ImageRequest.fromUri(url),
                    null
                )
                .subscribe(object : DataSubscriber<Void> {
                    override fun onNewResult(dataSource: DataSource<Void>) {
                        if (dataSource.isFinished) {
                            continuation.resume(true)
                        }
                    }

                    override fun onFailure(dataSource: DataSource<Void>) {
                        dataSource.failureCause?.let {
                            log(it) { "Error precaching photo: $url"}
                        }
                        continuation.resume(false)
                    }

                    override fun onCancellation(dataSource: DataSource<Void>) {
                        continuation.resume(false)
                    }

                    override fun onProgressUpdate(dataSource: DataSource<Void>) {
                    }
                }, Dispatchers.IO.asExecutor())
        }
}