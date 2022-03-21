package com.savvasdalkitsis.librephotos.auth.api

import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.auth.db.dao.AuthDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationHeaderInjector @Inject constructor(
    private val authDao: AuthDao,
    private val cookieManager: CookieManager,
) {

    fun inject(chain: Interceptor.Chain): Request {
        val accessToken = runBlocking { authDao.getAccessToken() }
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
