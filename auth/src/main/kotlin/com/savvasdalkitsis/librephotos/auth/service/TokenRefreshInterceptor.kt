package com.savvasdalkitsis.librephotos.auth.service

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
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
                    Authenticated -> chain.proceed(authenticationHeaderInjector.inject(chain))
                }
            }
            else -> response
        }
    }

    private fun Response.hasExpiredToken() = code == 401 || code == 403
}