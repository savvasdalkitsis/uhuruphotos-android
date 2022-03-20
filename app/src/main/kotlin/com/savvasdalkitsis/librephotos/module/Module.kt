package com.savvasdalkitsis.librephotos.module

import android.content.Context
import com.savvasdalkitsis.librephotos.auth.api.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.librephotos.network.DynamicDomainInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun okHttpBuilder(
        @ApplicationContext context: Context,
        authenticationHeaderInterceptor: AuthenticationHeaderInterceptor,
        dynamicDomainInterceptor: DynamicDomainInterceptor,
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .cache(
            Cache(
                File(context.cacheDir, "http_cache"),
                200 * 1024L * 1024L
            )
        )
        .addInterceptor(dynamicDomainInterceptor)
        .addInterceptor(authenticationHeaderInterceptor)
        .addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        )
}