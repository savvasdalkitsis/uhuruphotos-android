package com.savvasdalkitsis.librephotos.video.module

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.savvasdalkitsis.librephotos.auth.service.TokenRefreshInterceptor
import com.savvasdalkitsis.librephotos.auth.module.AuthModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VideoModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class VideoCache

    @Provides
    @Singleton
    @VideoCache
    fun videoCache(
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "video_cache"),
        300 * 1024L * 1024L
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