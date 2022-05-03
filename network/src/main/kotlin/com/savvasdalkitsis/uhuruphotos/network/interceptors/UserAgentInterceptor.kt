package com.savvasdalkitsis.uhuruphotos.network.interceptors

import com.savvasdalkitsis.uhuruphotos.network.BuildConfig.APP_VERSION_NAME
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UserAgentInterceptor @Inject constructor(
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request().newBuilder()
            .header(
                name = "User-agent",
                value = "UhuruPhotos/$APP_VERSION_NAME ${System.getProperty("http.agent")}"
            )
            .build()
        )
}
