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
package com.savvasdalkitsis.uhuruphotos.network.module

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.network.BasicOkHttpClient
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.mb
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.seconds
import com.savvasdalkitsis.uhuruphotos.network.interceptors.UserAgentInterceptor
import com.squareup.moshi.Moshi
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    @BasicOkHttpClient
    fun basicOkHttpBuilder(
        userAgentInterceptor: UserAgentInterceptor,
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .followRedirects(false)
        .callTimeout(60.seconds)
        .connectTimeout(60.seconds)
        .readTimeout(60.seconds)
        .writeTimeout(60.seconds)
        .addInterceptor(userAgentInterceptor)

    @Provides
    @Singleton
    fun httpCache(
        @ApplicationContext context: Context,
    ) = Cache(
        File(context.cacheDir, "http_cache"),
        50.mb
    )

    @Provides
    @Singleton
    fun retrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl("https://localhost/")
        .addConverterFactory(MoshiConverterFactory.create())

    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()
}