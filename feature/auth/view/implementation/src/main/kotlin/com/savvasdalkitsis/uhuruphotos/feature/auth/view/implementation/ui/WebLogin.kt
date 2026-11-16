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
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.error_logging_in

private const val USER_AGENT =
    "Mozilla/5.0 (Linux; Android 12; Pixel 6 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/" +
            "99.0.4844.88 Mobile Safari/537.36 OPR/68.2.3557.64219"

@Suppress("DEPRECATION")
@Composable
fun WebLogin(state: WebLoginState) {
    if (state.url == null) {
        UhuruFullLoading()
    } else {
        val webState = rememberWebViewState(url = state.url)
        val context = LocalContext.current
        val loginWebView = remember(context) { createLoginWebView(context) }
        UhuruScaffold { contentPadding ->
            Column {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                when (loginWebView) {
                    null -> NoContent(string.error_logging_in, refreshable = false)
                    else -> WebView(webState, factory = { loginWebView })
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createLoginWebView(context: Context): WebView? = try {
    WebView(context).apply {
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        settings.userAgentString = USER_AGENT
        settings.javaScriptEnabled = true
    }
} catch (_: RuntimeException) {
    null
}
