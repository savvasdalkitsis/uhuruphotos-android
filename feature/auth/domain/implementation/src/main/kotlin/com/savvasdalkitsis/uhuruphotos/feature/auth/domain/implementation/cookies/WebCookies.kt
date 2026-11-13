/*
Copyright 2026 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebCookies internal constructor(
    private val cookieManager: () -> CookieManager,
) {

    @Inject constructor() : this({ CookieManager.getInstance() })

    fun getCookie(url: String?): String? = url?.let {
        withCookieManager { getCookie(url) }
    }

    fun setCookie(url: String?, value: String) {
        url?.let {
            withCookieManager { setCookie(url, value) }
        }
    }

    private fun <T> withCookieManager(action: CookieManager.() -> T): T? = try {
        cookieManager().action()
    } catch (e: RuntimeException) {
        log(e)
        null
    }
}
