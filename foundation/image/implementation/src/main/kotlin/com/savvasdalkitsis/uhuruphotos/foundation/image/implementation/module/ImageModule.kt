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
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.FullImage
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.ThumbnailImage
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
    @FullImage
    fun fullImageMemoryCache(
        settingsUseCase: SettingsUseCase,
        @ApplicationContext context: Context,
    ): MemoryCache = MemoryCache.Builder(context)
        .maxSizeBytes(settingsUseCase.getLightboxPhotoMemCacheMaxLimit().mb)
        .build()

    @Provides
    @Singleton
    @ThumbnailImage
    fun thumbnailImageMemoryCache(
        settingsUseCase: SettingsUseCase,
        @ApplicationContext context: Context,
    ): MemoryCache = MemoryCache.Builder(context)
        .maxSizeBytes(settingsUseCase.getThumbnailMemCacheMaxLimit().mb)
        .build()

    @Provides
    @Singleton
    @FullImage
    fun fullImageDiskCache(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase,
    ): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache_full"))
        .maxSizeBytes(settingsUseCase.getLightboxPhotoDiskCacheMaxLimit().mb.toLong())
        .build()

    @Provides
    @Singleton
    @ThumbnailImage
    fun thumbnailImageDiskCache(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase,
    ): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache")) // keeping same name for backwards compatibility
        .maxSizeBytes(settingsUseCase.getThumbnailDiskCacheMaxLimit().mb.toLong())
        .build()

    @Provides
    @Singleton
    @FullImage
    fun fullImageLoader(
        @ApplicationContext context: Context,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
        @TokenRefreshInterceptor
        tokenRefreshInterceptor: Interceptor,
        @FullImage
        memoryCache: MemoryCache,
        @FullImage
        diskCache: DiskCache,
    ): ImageLoader = imageLoader(
        context, okHttpBuilder, tokenRefreshInterceptor, memoryCache, diskCache
    )

    @Provides
    @Singleton
    @ThumbnailImage
    fun thumbnailImageLoader(
        @ApplicationContext context: Context,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
        @TokenRefreshInterceptor
        tokenRefreshInterceptor: Interceptor,
        @ThumbnailImage
        memoryCache: MemoryCache,
        @ThumbnailImage
        diskCache: DiskCache,
    ): ImageLoader = imageLoader(
        context, okHttpBuilder, tokenRefreshInterceptor, memoryCache, diskCache
    )

    private fun imageLoader(
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

    private val Int.mb get() = coerceAtLeast(0) * 1024 * 1024

}