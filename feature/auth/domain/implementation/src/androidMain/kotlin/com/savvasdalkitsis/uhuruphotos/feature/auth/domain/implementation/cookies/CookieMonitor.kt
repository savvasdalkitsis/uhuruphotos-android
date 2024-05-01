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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.cookies.CookieMonitor
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CookieMonitor(
    private val serverUseCase: ServerUseCase,
) : CookieMonitor {

    override fun monitor(context: CoroutineContext): Job = CoroutineScope(Dispatchers.IO + context).launch {
        val cookies = CookieManager.getInstance()
        val server = serverUseCase.getServerUrl()
        var cookie: String?
        do {
            cookie = cookies.getCookie(server)
            log { "Cookie was: $cookie" }
            delay(500)
        } while (cookie?.contains("_forward_auth=") == false && !cookie.contains("authelia_session="))
    }
}