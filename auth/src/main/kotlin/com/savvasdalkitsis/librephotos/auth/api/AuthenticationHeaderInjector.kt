package com.savvasdalkitsis.librephotos.auth.api

import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.db.auth.TokenQueries
import okhttp3.Interceptor
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationHeaderInjector @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val cookieManager: CookieManager,
) {

    fun inject(chain: Interceptor.Chain): Request {
        val accessToken = tokenQueries.getAccessToken().executeAsOneOrNull()
        val cookie = cookieManager.getCookie(chain.request().url.toString())
        return chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .apply {
                if (!cookie.isNullOrEmpty()) {
                    header("Cookie", cookie)
                }
            }
            .build()
    }

}
