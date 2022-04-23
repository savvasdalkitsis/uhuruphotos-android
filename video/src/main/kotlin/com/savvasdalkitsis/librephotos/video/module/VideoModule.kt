package com.savvasdalkitsis.librephotos.video.module

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.savvasdalkitsis.librephotos.auth.module.AuthModule
import com.savvasdalkitsis.librephotos.auth.service.TokenRefreshInterceptor
import com.savvasdalkitsis.librephotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.librephotos.video.api.VideoCache
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