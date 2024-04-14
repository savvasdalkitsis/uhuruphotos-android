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
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.AuthenticatedOkHttpClient
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.VideoCache
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

@OptIn(UnstableApi::class)
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
    @Singleton
    fun cacheDataSourceFactory(
        @VideoOkHttp okHttpClient: OkHttpClient,
        @VideoCache cache: SimpleCache,
    ): CacheDataSource.Factory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(okHttpClient))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    @Provides
    @Singleton
    @VideoCache
    fun videoCache(
        @ApplicationContext context: Context,
        settingsUseCase: SettingsUseCase
    ): SimpleCache = SimpleCache(
        File(context.cacheDir, "video_cache"),
        LeastRecentlyUsedCacheEvictor(
            settingsUseCase.getVideoDiskCacheMaxLimit() * 1024L * 1024L
        ),
        StandaloneDatabaseProvider(context)
    )
}
