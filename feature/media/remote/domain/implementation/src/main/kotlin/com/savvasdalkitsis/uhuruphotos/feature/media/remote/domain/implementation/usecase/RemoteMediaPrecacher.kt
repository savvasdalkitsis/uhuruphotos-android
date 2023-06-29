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
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImage
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class RemoteMediaPrecacher @Inject constructor(
    @ApplicationContext val context: Context,
    @ThumbnailImage
    private val imageLoader: ImageLoader,
) : RemoteMediaPrecacher {

    override suspend fun precacheMedia(
        url: String,
        video: Boolean,
    ): Boolean = try {
        val result = imageLoader.execute(
            ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .target()
                .build()
        )
        if (result is ErrorResult) {
            log(result.throwable) { "Error precaching thumbnail: $url" }
        }
        result is SuccessResult
    } catch (e: Exception) {
        log(e)
        false
    }

}