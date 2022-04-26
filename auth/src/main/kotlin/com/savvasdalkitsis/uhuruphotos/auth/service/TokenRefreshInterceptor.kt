package com.savvasdalkitsis.uhuruphotos.auth.service

import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.usecase.AuthenticationUseCase
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