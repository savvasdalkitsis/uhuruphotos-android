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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.WebLoginNavigationTarget.Companion.url
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions.LoadPage
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions.WebLoginAction
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLogin
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.viewmodel.WebLoginViewModel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import javax.inject.Inject

class WebLoginNavigationTarget @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<WebLoginState, WebLoginAction, WebLoginViewModel>(
            route = WebLoginNavigationTarget.registrationName,
            themeMode = settingsUseCase.observeThemeModeState(),
            initializer = { navBackStackEntry, actions ->
                actions(LoadPage(navBackStackEntry.url))
            }
        ) { state, _ ->
            WebLogin(state)
        }
    }
}