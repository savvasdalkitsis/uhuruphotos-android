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

import android.content.Context
import android.webkit.CookieManager
import androidx.credentials.CredentialManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.AuthenticatedOkHttpClient
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
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
    fun retrofit(
        retrofitBuilder: Retrofit.Builder,
        httpCache: Cache,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .cache(httpCache)
            .addInterceptor(tokenRefreshInterceptor)
            .build()
        )
        .build()

    @Provides
    @Singleton
    fun authenticationService(
        @RetrofitWithoutTokenRefresh retrofit: Retrofit,
    ): AuthenticationService = retrofit.create(AuthenticationService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    internal annotation class RetrofitWithoutTokenRefresh

    @Provides
    @Singleton
    @RetrofitWithoutTokenRefresh
    fun retrofitWithoutTokenRefresh(
        httpCache: Cache,
        retrofitBuilder: Retrofit.Builder,
        @AuthenticatedOkHttpClient
        okHttpBuilder: OkHttpClient.Builder,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .cache(httpCache)
            .build())
        .build()

    @Provides
    fun credentialManager(
        @ApplicationContext context: Context,
    ): CredentialManager = CredentialManager.create(context)
}