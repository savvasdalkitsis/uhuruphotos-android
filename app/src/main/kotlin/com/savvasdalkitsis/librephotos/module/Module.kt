package com.savvasdalkitsis.librephotos.module

import com.savvasdalkitsis.librephotos.auth.api.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.librephotos.auth.api.WebLoginInterceptor
import com.savvasdalkitsis.librephotos.network.DynamicDomainInterceptor
import com.savvasdalkitsis.librephotos.web.WebkitCookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun okHttpBuilder(
        authenticationHeaderInterceptor: AuthenticationHeaderInterceptor,
        dynamicDomainInterceptor: DynamicDomainInterceptor,
        webLoginInterceptor: WebLoginInterceptor,
        webkitCookieManager: WebkitCookieManager,
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .followRedirects(false)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .cookieJar(webkitCookieManager)
        .addInterceptor(dynamicDomainInterceptor)
        .addInterceptor(webLoginInterceptor)
        .addInterceptor(authenticationHeaderInterceptor)
        .addInterceptor(HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
        )
}