package com.savvasdalkitsis.librephotos.web

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject

class WebkitCookieManager @Inject constructor(
    private val cookieManager: CookieManager,
) : CookieJar {

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieManager.setCookie(url.toString(), cookie.toString())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        when (val cookies = cookieManager.getCookie(url.toString())) {
            null -> emptyList()
            else -> cookies.split("; ").mapNotNull { Cookie.parse(url, it) }
        }
}