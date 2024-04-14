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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.module

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.AuthenticatedOkHttpClient
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.TokenRefreshOkHttpClient
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies.WebkitCookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.network.DynamicDomainInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.WebLoginInterceptor
import com.savvasdalkitsis.uhuruphotos.foundation.network.api.OkHttpBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType.Application
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.serialization
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun cookieManager(): CookieManager = CookieManager.getInstance()

    @Provides
    @AuthenticatedOkHttpClient
    fun authenticatedOkHttpBuilder(
        baseBuilder: OkHttpBuilder,
        authenticationHeaderInterceptor: AuthenticationHeaderInterceptor,
        dynamicDomainInterceptor: DynamicDomainInterceptor,
        webLoginInterceptor: WebLoginInterceptor,
        webkitCookieManager: WebkitCookieManager,
    ): OkHttpClient.Builder = baseBuilder.build {
        cookieJar(webkitCookieManager)
            .addInterceptor(dynamicDomainInterceptor)
            .addInterceptor(webLoginInterceptor)
            .addInterceptor(authenticationHeaderInterceptor)
    }

    @Provides
    @Singleton
    fun ktorfit(
        ktorfitBuilder: Ktorfit.Builder,
        httpCache: Cache,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): Ktorfit {
        return ktorfitBuilder
            .httpClient(fromOkHttp(
                okHttpBuilder.addInterceptor(tokenRefreshInterceptor),
                httpCache)
            )
            .build()
    }

    @Provides
    @Singleton
    @TokenRefreshOkHttpClient
    fun tokenRefreshOkHttpClient(
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): OkHttpClient = okHttpBuilder
        .addInterceptor(tokenRefreshInterceptor)
        .build()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    internal annotation class KtorfitWithoutTokenRefresh

    @Provides
    @Singleton
    fun authenticationService(
        @KtorfitWithoutTokenRefresh ktorfit: Ktorfit,
    ): AuthenticationService = ktorfit.create()

    @Provides
    @Singleton
    @KtorfitWithoutTokenRefresh
    fun ktorfitWithoutTokenRefresh(
        httpCache: Cache,
        ktorfitBuilder: Ktorfit.Builder,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): Ktorfit = ktorfitBuilder
        .httpClient(fromOkHttp(okHttpBuilder, httpCache))
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private fun fromOkHttp(okHttpBuilder: OkHttpClient.Builder, httpCache: Cache): HttpClient =
        HttpClient(OkHttp) {
            val json = Json {
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
                ignoreUnknownKeys = true
                explicitNulls = false
            }
            install(DefaultRequest) {
                contentType(Application.Json)
            }
            install(ContentNegotiation) {
                serialization(Application.Any, json)
                serialization(Application.Json, json)
            }
            engine {
                preconfigured = okHttpBuilder
                    .cache(httpCache)
                    .build()
            }
        }
}