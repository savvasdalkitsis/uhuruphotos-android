package com.savvasdalkitsis.librephotos.auth.api

import com.savvasdalkitsis.librephotos.auth.api.model.CallErrorResponse
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.module.Module
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class TokenRefreshInterceptor @Inject constructor(
    @Module.CallErrorResponseAdapter
    private val adapter: JsonAdapter<CallErrorResponse>,
    private val authenticationUseCase: AuthenticationUseCase,
    private val authenticationHeaderInjector: AuthenticationHeaderInjector,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return when {
            response.hasExpiredToken() -> when (runBlocking { authenticationUseCase.refreshToken() }) {
                AuthStatus.Unauthenticated -> throw AccessTokenRefreshError()
                AuthStatus.Authenticated -> {
                    val request = authenticationHeaderInjector.inject(chain)

                    chain.proceed(request)
                }
            }
            else -> response
        }
    }

    private fun Response.hasExpiredToken(): Boolean = code == 401 || code == 403
//    when {
//        code != 401 && code != 403 -> false
//        else -> {
//            val body = peekBody(512L).string()
//            val error = adapter.fromJson(body)
//            error?.code == "token_not_valid" && error.messages.any {
//                it.tokenType == "access"
//            }
//        }
//    }
}