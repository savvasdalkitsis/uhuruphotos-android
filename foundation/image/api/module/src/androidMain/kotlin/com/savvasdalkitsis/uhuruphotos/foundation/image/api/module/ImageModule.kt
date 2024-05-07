/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.image.api.module

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.cache.ImageCacheModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.initializer.ImageInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCallbacks
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object ImageModule {

    val fullImageLoader: ImageLoader by singleInstance {
        imageLoader(
            AndroidModule.applicationContext,
            AuthModule.authenticatedOkHttpBuilder,
            AuthModule.tokenRefreshInterceptor,
            ImageCacheModule.fullImageMemoryCache,
            ImageCacheModule.fullImageDiskCache,
        )
    }

    val thumbnailImageLoader: ImageLoader by singleInstance {
        imageLoader(
            AndroidModule.applicationContext,
            AuthModule.authenticatedOkHttpBuilder,
            AuthModule.tokenRefreshInterceptor,
            ImageCacheModule.thumbnailImageMemoryCache,
            ImageCacheModule.thumbnailImageDiskCache,
        )
    }

    val thumbnailImageWithNetworkCacheSupportLoader: ImageLoader by singleInstance {
        thumbnailImageLoader.newBuilder()
            .respectCacheHeaders(true)
            .build()
    }

    val imageInitializer: ApplicationCallbacks by singleInstance {
        ImageInitializer(
            fullImageLoader,
            AuthModule.authenticatedOkHttpBuilder,
            AuthModule.tokenRefreshInterceptor,
        )
    }

    private fun imageLoader(
        context: Context,
        okHttpBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: Interceptor,
        memoryCache: MemoryCache,
        diskCache: DiskCache,
    ): ImageLoader = ImageLoader.Builder(context)
        .memoryCache { memoryCache }
        .diskCache { diskCache }
        .okHttpClient(okHttpBuilder
            .addInterceptor(tokenRefreshInterceptor)
            .build())
        .crossfade(true)
        .respectCacheHeaders(false)
        .components {
            add(VideoFrameDecoder.Factory())
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
}