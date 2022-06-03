/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.auth.service

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.api.db.auth.TokenQueries
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
