package com.savvasdalkitsis.librephotos.auth.service

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthenticationHeaderInterceptor @Inject constructor(
    private val authenticationHeaderInjector: AuthenticationHeaderInjector,
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(authenticationHeaderInjector.inject(chain))

}
