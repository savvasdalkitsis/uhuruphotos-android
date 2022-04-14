package com.savvasdalkitsis.librephotos.server.network

import com.savvasdalkitsis.librephotos.auth.usecase.ServerUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class DynamicDomainInterceptor @Inject constructor(
    private val serverUseCase: ServerUseCase,
) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            request.newBuilder()
                .url(request.url.toString()
                    .replace("https://localhost",
                        runBlocking { serverUseCase.getServerUrl() }!!
                    )
                )
                .build()
        )
    }
}