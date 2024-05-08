/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.cookies.CookieMonitor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies.WebkitCookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.network.DynamicDomainInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationHeaderInjector
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.WebLoginInterceptor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.JwtUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.cache.ImageCacheModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.network.api.module.NetworkModule
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.module.cache.VideoCacheModule
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.module.WorkerModule
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
import okhttp3.Interceptor
import okhttp3.OkHttpClient

object PlatformAuthModule {

    val cookieMonitor: CookieMonitor by singleInstance {
        com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies.CookieMonitor(
            serverUseCase,
        )
    }

    val authenticatedOkHttpBuilder: OkHttpClient.Builder
        get() = NetworkModule.okHttpBuilder.build {
            cookieJar(webkitCookieManager)
                .addInterceptor(dynamicDomainInterceptor)
                .addInterceptor(webLoginInterceptor)
                .addInterceptor(authenticationHeaderInterceptor)
        }

    val ktorfit: Ktorfit by singleInstance {
        NetworkModule.ktorfitBuilder
            .httpClient(fromOkHttp(
                authenticatedOkHttpBuilder.addInterceptor(tokenRefreshInterceptor),
                NetworkModule.httpCache,
            ))
            .build()
    }

    val tokenRefreshInterceptor: Interceptor
        get() = TokenRefreshInterceptor(authenticationUseCase, authenticationHeaderInjector)

    val serverUseCase: com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
        get() = ServerUseCase(PreferencesModule.plainTextPreferences)

    val authenticationUseCase: AuthenticationUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationUseCase(
            authenticationService,
            jwtUseCase,
            authenticationRepository,
            authenticationLoginUseCase
        )

    val authenticationLoginUseCase: AuthenticationLoginUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationLoginUseCase(
            authenticationService,
            authenticationRepository,
            DbModule.database,
            ImageCacheModule.imageCacheUseCase,
            VideoCacheModule.videoCacheUseCase,
            WorkerModule.workScheduleUseCase,
        )

    val authenticationHeadersUseCase: AuthenticationHeadersUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationHeadersUseCase(
            authenticationRepository, cookieManager
        )

    private val cookieManager: CookieManager by singleInstance {
        CookieManager.getInstance()
    }

    private val webkitCookieManager: WebkitCookieManager
        get() = WebkitCookieManager(cookieManager)

    private val authenticationService: AuthenticationService by singleInstance {
        ktorfitWithoutTokenRefresh.create()
    }

    private val ktorfitWithoutTokenRefresh: Ktorfit by singleInstance {
        NetworkModule.ktorfitBuilder
            .httpClient(fromOkHttp(authenticatedOkHttpBuilder, NetworkModule.httpCache))
            .build()
    }

    private val webLoginInterceptor: Interceptor
        get() = WebLoginInterceptor(NavigationModule.navigator, serverUseCase, cookieManager)

    private val dynamicDomainInterceptor: Interceptor
        get() = DynamicDomainInterceptor(serverUseCase)

    private val authenticationHeaderInterceptor: Interceptor
        get() = AuthenticationHeaderInterceptor(authenticationHeaderInjector)

    private val authenticationHeaderInjector: AuthenticationHeaderInjector by singleInstance {
        AuthenticationHeaderInjector(authenticationHeadersUseCase)
    }

    private val authenticationRepository: AuthenticationRepository
        get() = AuthenticationRepository(
            DbModule.database.tokenQueries,
            jwtUseCase,
            preferences = PreferencesModule.plainTextPreferences,
            encryptedPreferences = PreferencesModule.encryptedPreferences
        )

    private val jwtUseCase: JwtUseCase
        get() = JwtUseCase()

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