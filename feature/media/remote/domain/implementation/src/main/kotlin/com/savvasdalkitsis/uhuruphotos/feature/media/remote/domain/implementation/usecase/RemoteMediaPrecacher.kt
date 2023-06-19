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
import android.net.Uri
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asyncReturn
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class RemoteMediaPrecacher @Inject constructor(
    @ApplicationContext val context: Context,
    private val imageLoader: ImageLoader,
    private val videoCache: CacheDataSource.Factory,
) : RemoteMediaPrecacher {

    override suspend fun precacheMedia(
        url: String,
        video: Boolean,
        progressListener: (progress: Int) -> Unit,
    ): Boolean = try {
        if (video) {
            cacheVideo(url, progressListener)
        } else {
            cachePhoto(url)
        }
    } catch (e: Exception) {
        log(e)
        false
    }

    private suspend fun cacheVideo(url: String, progressListener: (Int) -> Unit): Boolean =
        asyncReturn {
            try {
                CacheWriter(
                    videoCache.createDataSourceForDownloading(),
                    DataSpec(Uri.parse(url)),
                    ByteArray(8 * 1024 * 1024)
                ) { requestLength, bytesCached, _ ->
                    progressListener(((bytesCached / requestLength.toFloat()) * 100).toInt())
                }.cache()
                true
            } catch (e: Exception) {
                log(e) { "Error precaching video: $url"}
                false
            }
        }

    private suspend fun cachePhoto(url: String): Boolean {
        val result = imageLoader.execute(
            ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .target()
                .build()
        )
        if (result is ErrorResult) {
            log(result.throwable) { "Error precaching photo: $url"}
        }

        return result is SuccessResult
    }
}