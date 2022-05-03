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
package com.savvasdalkitsis.uhuruphotos.video.module

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.savvasdalkitsis.uhuruphotos.auth.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.auth.service.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.video.api.VideoCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VideoModule {

    @Provides
    @Singleton
    @VideoCache
    fun videoCache(
        settingsUseCase: SettingsUseCase,
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "video_cache"),
        settingsUseCase.getVideoDiskCacheMaxLimit() * 1024L * 1024L
    )

    @Provides
    fun exoplayer(
        @ApplicationContext context: Context,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        @AuthModule.AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
        @VideoCache
        cache: Cache,
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(OkHttpDataSource.Factory(okHttpBuilder
            .cache(cache)
            .addInterceptor(tokenRefreshInterceptor)
            .build())))
        .build()
}