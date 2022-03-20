package com.savvasdalkitsis.librephotos.network

import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

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