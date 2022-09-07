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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.network

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicDomainInterceptor @Inject constructor(
    private val serverUseCase: ServerUseCase,
) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return when (val serverUrl = runBlocking { serverUseCase.getServerUrl() }) {
            null -> throw IOException("Server url is not initialised")
            else -> chain.proceed(
                request
                    .newBuilder()
                    .url(request.url.replace(serverUrl))
                    .build()
                )
        }
    }

    private fun HttpUrl.replace(url: String) = toString()
            .replace("https://localhost", url)
}