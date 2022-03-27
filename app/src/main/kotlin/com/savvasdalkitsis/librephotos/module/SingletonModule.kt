package com.savvasdalkitsis.librephotos.module

import android.content.Context
import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.Database
import com.savvasdalkitsis.librephotos.auth.api.AuthenticationService
import com.savvasdalkitsis.librephotos.auth.api.TokenRefreshInterceptor
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
    ): AuthenticationService = retrofit.create(AuthenticationService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthenticationRetrofit
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpCache
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ImageCache

    @Provides
    @Singleton
    @HttpCache
    fun httpCache(
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "http_cache"),
        50 * 1024L * 1024L
    )

    @Provides
    @Singleton
    @ImageCache
    fun imageCache(
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "image_cache"),
        600 * 1024L * 1024L
    )

    @Provides
    @Singleton
    @AuthenticationRetrofit
    fun authenticationRetrofit(
        @HttpCache httpCache: Cache,
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
        @HttpCache httpCache: Cache,
        retrofitBuilder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
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
}