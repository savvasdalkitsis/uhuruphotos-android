/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import kotlinx.coroutines.runBlocking
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class AuthenticationHeadersUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val cookieManager: CookieManager,
) : AuthenticationHeadersUseCase {

    override fun headers(requestUrl: String): Set<Pair<String, String>> {
        val accessToken = runBlocking { authenticationRepository.getAccessToken() }
        val cookie = cookieManager.getCookie(requestUrl)
        return setOf(
            "Authorization" to "Bearer $accessToken",
        ) + setOfNotNull(("Cookie" to cookie).takeIf { !cookie.isNullOrEmpty() })
    }
}