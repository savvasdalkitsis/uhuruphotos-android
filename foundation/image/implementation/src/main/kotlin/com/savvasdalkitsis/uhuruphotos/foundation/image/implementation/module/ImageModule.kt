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
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.core.ImageTranscoderType
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
    fun coilMemoryCache(
        settingsUseCase: SettingsUseCase,
        @ApplicationContext context: Context,
    ): MemoryCache = MemoryCache.Builder(context)
        .maxSizeBytes((settingsUseCase.getLightboxPhotoMemCacheMaxLimit() * 1024 * 1024).coerceAtLeast(0))
        .build()

    @Provides
    @Singleton
    fun coilDiskCache(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase,
    ): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(settingsUseCase.getLightboxPhotoDiskCacheMaxLimit().coerceAtLeast(0) * 1024L * 1024L)
        .build()

    @Provides
    @Singleton
    fun frescoMemoryCache(
        settingsUseCase: SettingsUseCase,
    ): MemoryCacheParams = MemoryCacheParams(
        maxCacheSize = (settingsUseCase.getThumbnailMemCacheMaxLimit() * 1024 * 1024).coerceAtLeast(0),
        maxCacheEntries = Int.MAX_VALUE,
        maxEvictionQueueSize = Int.MAX_VALUE,
        maxEvictionQueueEntries = Int.MAX_VALUE,
        maxCacheEntrySize = Int.MAX_VALUE
    )

    @Provides
    @Singleton
    fun frescoDiskCacheConfig(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase,
    ): DiskCacheConfig = DiskCacheConfig.newBuilder(context)
        .setBaseDirectoryPath(context.cacheDir.resolve("image_cache_fresco_small"))
        .setMaxCacheSize(settingsUseCase.getThumbnailDiskCacheMaxLimit().coerceAtLeast(0) * 1024L * 1024L)
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

    @Provides
    @Singleton
    fun imagePipelineConfig(
        @ApplicationContext context: Context,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
        @TokenRefreshInterceptor
        tokenRefreshInterceptor: Interceptor,
        diskCacheConfig: DiskCacheConfig,
        memoryCache: MemoryCacheParams
    ): ImagePipelineConfig =
        OkHttpImagePipelineConfigFactory
            .newBuilder(context, okHttpBuilder
                .addInterceptor(tokenRefreshInterceptor)
                .build())
            .setDiskCacheEnabled(true)
            .setMainDiskCacheConfig(diskCacheConfig)
            .setIsPrefetchEnabledSupplier { true }
            .setBitmapMemoryCacheParamsSupplier { memoryCache }
            .setDownsampleEnabled(true)
            .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
            .experiment().setNativeCodeDisabled(true)
            .setResizeAndRotateEnabledForNetwork(true)
            .build()
}