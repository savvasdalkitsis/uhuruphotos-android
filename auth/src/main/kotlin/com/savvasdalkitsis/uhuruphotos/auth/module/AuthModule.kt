package com.savvasdalkitsis.uhuruphotos.auth.module

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.auth.network.DynamicDomainInterceptor
import com.savvasdalkitsis.uhuruphotos.auth.service.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.uhuruphotos.auth.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.auth.service.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.auth.service.WebLoginInterceptor
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.WebkitCookieManager
import com.savvasdalkitsis.uhuruphotos.db.Database
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.network.module.OkHttpBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthenticatedOkHttpClient

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
}