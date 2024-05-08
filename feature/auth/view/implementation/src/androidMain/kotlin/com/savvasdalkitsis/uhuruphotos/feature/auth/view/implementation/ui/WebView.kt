/*
Copyright 2024 Savvas Dalkitsis

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

import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.state.WebViewSettings
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.state.WebViewState

@Composable
actual fun WebView(state: WebViewState, settings: (WebViewSettings) -> Unit) {
    com.google.accompanist.web.WebView(state, onCreated = {
        settings(AndroidWebViewSettings(it))
    })
}

private class AndroidWebViewSettings(
    private val webView: WebView,
) : WebViewSettings {
    override fun setAcceptThirdPartyCookies(accept: Boolean) {
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
    }

    override fun setUserAgentString(userAgent: String) {
        webView.settings.userAgentString = userAgent
    }

    override fun setJavaScriptEnabled(enabled: Boolean) {
        webView.settings.javaScriptEnabled = enabled
    }

}