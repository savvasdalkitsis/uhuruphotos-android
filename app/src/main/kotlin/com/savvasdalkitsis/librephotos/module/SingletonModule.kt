package com.savvasdalkitsis.librephotos.module

import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.token.db.Token
import com.squareup.moshi.Moshi
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SingletonModule {

    @Provides
    @Singleton
    fun driver(@ApplicationContext context: Context): SqlDriver =
        AndroidSqliteDriver(Database.Schema, context, "librePhotos.db")

    @Provides
    @Singleton
    fun database(driver: SqlDriver) = Database(
        driver = driver,
        tokenAdapter = Token.Adapter(typeAdapter = EnumColumnAdapter())
    )

    @Provides
    @Singleton
    fun authenticationService(
        @AuthenticationRetrofit retrofit: Retrofit,
    ): com.savvasdalkitsis.librephotos.auth.api.AuthenticationService = retrofit.create(com.savvasdalkitsis.librephotos.auth.api.AuthenticationService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthenticationRetrofit

    @Provides
    @Singleton
    fun httpCache(
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "http_cache"),
        50 * 1024L * 1024L
    )

    @Provides
    @Singleton
    @AuthenticationRetrofit
    fun authenticationRetrofit(
        httpCache: Cache,
        retrofitBuilder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .cache(httpCache)
            .build())
        .build()

    @Provides
    @Singleton
    fun retrofit(
        httpCache: Cache,
        retrofitBuilder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: com.savvasdalkitsis.librephotos.auth.api.TokenRefreshInterceptor,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .cache(httpCache)
            .addInterceptor(tokenRefreshInterceptor)
            .build())
        .build()

    @Provides
    @Singleton
    fun retrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl("https://localhost/")
        .addConverterFactory(MoshiConverterFactory.create())

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
            .build()

    @Provides
    @Singleton
    fun cookieManager(): CookieManager = CookieManager.getInstance()

    @Provides
    @Singleton
    fun memoryCache(
        @ApplicationContext context: Context,
    ): MemoryCache = MemoryCache.Builder(context)
        .maxSizePercent(0.25)
        .build()

    @Provides
    @Singleton
    fun diskCache(
        @ApplicationContext context: Context,
    ): DiskCache = DiskCache.Builder()
        .directory(context.cacheDir.resolve("image_cache"))
        .maxSizeBytes(250 * 1024 * 1024)
        .build()

    @Provides
    @Singleton
    fun imageLoader(
        @ApplicationContext context: Context,
        okHttpBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: com.savvasdalkitsis.librephotos.auth.api.TokenRefreshInterceptor,
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