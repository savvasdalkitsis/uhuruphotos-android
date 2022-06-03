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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.service

import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class WebLoginInterceptor @Inject constructor(
    private val navigator: Navigator,
    private val serverUseCase: ServerUseCase,
    private val cookieManager: CookieManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 307) {
            CoroutineScope(Dispatchers.Main).launch {
                cookieManager.setCookie(serverUseCase.getServerUrl(), "")
                navigator.navigateTo(
                    WebLoginNavigationTarget.name(response.header("Location")!!)
                )
            }
        }
        return response
    }

}