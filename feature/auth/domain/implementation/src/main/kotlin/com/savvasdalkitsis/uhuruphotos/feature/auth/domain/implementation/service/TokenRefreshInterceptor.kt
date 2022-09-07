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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val authenticationHeaderInjector: AuthenticationHeaderInjector,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return when {
            response.hasExpiredToken() -> {
                response.close()
                when (runBlocking { authenticationUseCase.refreshToken() }) {
                    Unauthenticated -> throw AccessTokenRefreshError()
                    else -> chain.proceed(authenticationHeaderInjector.inject(chain))
                }
            }
            else -> response
        }
    }

    private fun Response.hasExpiredToken() = code == 401 || code == 403
}