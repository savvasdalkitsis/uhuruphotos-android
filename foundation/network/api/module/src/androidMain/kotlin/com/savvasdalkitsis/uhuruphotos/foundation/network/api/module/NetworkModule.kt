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
package com.savvasdalkitsis.uhuruphotos.foundation.network.api.module

import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.interceptors.UserAgentInterceptor
import com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.logging.HttpLogger
import com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.OkHttpBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.module.SystemModule
import de.jensklingenberg.ktorfit.Ktorfit
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

object NetworkModule {

    val okHttpBuilder: com.savvasdalkitsis.uhuruphotos.foundation.network.api.OkHttpBuilder
        get() = OkHttpBuilder(basicOkHttpBuilder, httpLogger)

    private val httpLogger: HttpLoggingInterceptor.Logger
        get ()= HttpLogger()

    val basicOkHttpBuilder: OkHttpClient.Builder
        get() = OkHttpClient().newBuilder()
            .followRedirects(false)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(userAgentInterceptor)

    val httpCache: Cache by singleInstance {
        Cache(
            File(AndroidModule.applicationContext.cacheDir, "http_cache"),
            50 * 1024L * 1024L
        )
    }

    private val userAgentInterceptor: UserAgentInterceptor
        get() = UserAgentInterceptor(SystemModule.applicationUseCase)

    val ktorfitBuilder: Ktorfit.Builder get() = Ktorfit.Builder()
        .baseUrl("https://localhost/")
}