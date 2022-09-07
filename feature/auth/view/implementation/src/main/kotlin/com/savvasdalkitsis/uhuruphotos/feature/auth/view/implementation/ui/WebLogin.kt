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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui

import android.annotation.SuppressLint
import android.webkit.CookieManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullProgressBar

private const val USER_AGENT =
    "Mozilla/5.0 (Linux; Android 12; Pixel 6 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/" +
            "99.0.4844.88 Mobile Safari/537.36 OPR/68.2.3557.64219"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebLogin(state: WebLoginState) {
    if (state.url == null) {
        FullProgressBar()
    } else {
        val webState = rememberWebViewState(url = state.url)
        CommonScaffold { contentPadding ->
            Column {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                WebView(
                    webState,
                    onCreated = {
                        CookieManager.getInstance().setAcceptThirdPartyCookies(it, true)
                        with(it.settings) {
                            userAgentString =
                                com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.USER_AGENT
                            javaScriptEnabled = true
                        }
                    }
                )
            }
        }
    }
}