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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.navigation

import android.util.Base64
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.seam.WebEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.seam.WebLoginAction
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.seam.WebLoginEffect
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.ui.WebLogin
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.implementation.auth.weblogin.weblogin.viewmodel.WebLoginViewModel
import javax.inject.Inject

class WebLoginNavigationTarget @Inject constructor(
    private val effectsHandler: WebEffectsHandler,
    private val settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<WebLoginState, WebLoginEffect, WebLoginAction, WebLoginViewModel>(
            name = name,
            effects = effectsHandler,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, actions ->
                actions(WebLoginAction.LoadPage(navBackStackEntry.url))
            },
            createModel = { hiltViewModel() }
        ) { state, _ ->
            WebLogin(state)
        }
    }

    companion object {
        private const val name = "web-login/{url}"
        fun name(url: String) = name.replace(
            "{url}", Base64.encodeToString(url.toByteArray(), Base64.URL_SAFE)
        )
        private val NavBackStackEntry.url : String get() {
            val encodedUrl = arguments!!.getString("url")!!
            val url = Base64.decode(encodedUrl, Base64.URL_SAFE)
            return String(url)
        }
    }
}