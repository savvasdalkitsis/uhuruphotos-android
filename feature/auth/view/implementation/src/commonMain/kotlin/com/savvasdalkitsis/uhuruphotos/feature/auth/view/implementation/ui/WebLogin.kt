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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.state.USER_AGENT
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.state.WebLoginState
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.state.rememberWebViewState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold

@Composable
fun WebLogin(state: WebLoginState) {
    if (state.url == null) {
        FullLoading()
    } else {
        val webState = rememberWebViewState(state.url)
        CommonScaffold { contentPadding ->
            Column {
                Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                WebView(
                    webState
                ) {
                    it.setAcceptThirdPartyCookies(true)
                    it.setUserAgentString(USER_AGENT)
                    it.setJavaScriptEnabled(true)
                }
            }
        }
    }
}