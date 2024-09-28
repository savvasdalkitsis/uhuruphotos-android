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
import coil.annotation.ExperimentalCoilApi
import coil.decode.DataSource
import coil.request.CachePolicy.DISABLED
import coil.request.CachePolicy.ENABLED
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel.CHANGED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel.SKIPPED
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImage
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@OptIn(ExperimentalCoilApi::class)
@AutoBind
class RemoteMediaPrecacher @Inject constructor(
    @ApplicationContext val context: Context,
    @ThumbnailImage
    private val imageLoader: ImageLoader,
) : RemoteMediaPrecacher {

    override suspend fun precacheMedia(
        url: String,
        video: Boolean,
    ): Result<MediaOperationResultModel, Throwable> = try {
        when {
            inDiskCache(url) -> Ok(SKIPPED)
            else -> when (val result = fetch(url)) {
                is ErrorResult -> {
                    log(result.throwable) { "Error precaching thumbnail: $url" }
                    Err(result.throwable)
                }

                is SuccessResult -> Ok(
                    if (result.dataSource == DataSource.NETWORK)
                        CHANGED
                    else
                        SKIPPED
                )
            }
        }
    } catch (e: Exception) {
        log(e)
        Err(e)
    }

    private fun inDiskCache(url: String) =
        imageLoader.diskCache?.openSnapshot(url)?.use { true } ?: false

    private suspend fun fetch(url: String) = imageLoader.execute(
        ImageRequest.Builder(context)
            .data(url)
            .memoryCachePolicy(DISABLED)
            .diskCachePolicy(ENABLED)
            .target()
            .build()
    )
}