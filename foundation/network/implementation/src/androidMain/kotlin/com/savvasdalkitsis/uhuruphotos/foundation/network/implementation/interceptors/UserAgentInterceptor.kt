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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.interceptors

import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.ApplicationUseCase
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class UserAgentInterceptor @Inject constructor(
    applicationUseCase: ApplicationUseCase,
): Interceptor {

    private val versionName = applicationUseCase.appVersion()

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().newBuilder()
            .header(
                name = "User-agent",
                value = "UhuruPhotos/$versionName ${System.getProperty("http.agent")}"
            )
            .build()
        )
}
