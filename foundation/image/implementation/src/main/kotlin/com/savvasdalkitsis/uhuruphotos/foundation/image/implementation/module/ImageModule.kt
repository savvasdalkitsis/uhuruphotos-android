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
package com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.module

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.AuthenticatedOkHttpClient
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImageModule {

    @Provides
    @Singleton
    fun memoryCache(
        settingsUseCase: SettingsUseCase,
        @ApplicationContext context: Context,
    ): MemoryCache = MemoryCache.Builder(context)
        .maxSizeBytes((settingsUseCase.getImageMemCacheMaxLimit() * 1024 * 1024).coerceAtLeast(0))
        .build()

    @Provides
    @Singleton
    fun diskCache(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase,
    ): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(settingsUseCase.getImageDiskCacheMaxLimit().coerceAtLeast(0) * 1024L * 1024L)
        .build()

    @Provides
    @Singleton
    fun imageLoader(
        @ApplicationContext context: Context,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
        @TokenRefreshInterceptor
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