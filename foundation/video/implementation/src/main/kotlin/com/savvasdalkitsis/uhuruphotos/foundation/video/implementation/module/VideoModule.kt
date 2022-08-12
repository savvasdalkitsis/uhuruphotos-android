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
package com.savvasdalkitsis.uhuruphotos.foundation.video.implementation.module

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.savvasdalkitsis.uhuruphotos.api.auth.AuthenticatedOkHttpClient
import com.savvasdalkitsis.uhuruphotos.api.auth.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalContentExoplayer
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.VideoOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VideoModule {

    @Provides
    @Singleton
    @VideoOkHttp
    fun videoOkHttp(
        @TokenRefreshInterceptor
        tokenRefreshInterceptor: Interceptor,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): OkHttpClient = okHttpBuilder
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .addInterceptor(tokenRefreshInterceptor)
        .build()

    @Provides
    fun exoplayer(
        @ApplicationContext context: Context,
        cacheDataSourceFactory: CacheDataSource.Factory,
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
        .build()

    @Provides
    @LocalContentExoplayer
    fun localContentExoplayer(
        @ApplicationContext context: Context,
    ): ExoPlayer = ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun cacheDataSourceFactory(
        @ApplicationContext context: Context,
        @VideoOkHttp okHttpClient: OkHttpClient,
        settingsUseCase: SettingsUseCase,
    ): CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(
            SimpleCache(
                File(context.cacheDir, "video_cache"),
                LeastRecentlyUsedCacheEvictor(
                    settingsUseCase.getVideoDiskCacheMaxLimit() * 1024L * 1024L
                ),
                StandaloneDatabaseProvider(context)
            )
        )
        .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(okHttpClient))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}