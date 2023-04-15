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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffect.NavigateToServerEdit
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.seam.AccountOverviewEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.server.view.api.navigation.ServerNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.api.navigation.SettingsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import javax.inject.Inject

class AccountOverviewEffectHandler @Inject constructor(
    private val navigator: Navigator,
) : EffectHandler<AccountOverviewEffect> {

    override suspend fun handleEffect(effect: AccountOverviewEffect) = when (effect) {
        NavigateToServerEdit -> {
            navigator.navigateTo(
                ServerNavigationRoute(auto = false)
            )
        }
        NavigateToSettings -> {
            navigator.navigateTo(SettingsNavigationRoute)
        }
        ReloadApp -> with(navigator) {
            clearBackStack()
            navigateTo(HomeNavigationRoute)
        }
    }

}