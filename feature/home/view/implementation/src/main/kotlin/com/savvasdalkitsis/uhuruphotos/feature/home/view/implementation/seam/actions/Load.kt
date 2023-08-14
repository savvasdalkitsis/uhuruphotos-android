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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeMutation
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.api.navigation.NotificationsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.server.view.api.navigation.ServerNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.api.navigation.WelcomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

data object Load : HomeAction() {

    context(HomeActionsContext) override fun handle(
        state: HomeState
    ) = flow {
        emit(HomeMutation.Loading)
        val proceed = when {
            settingsUseCase.getBiometricsRequiredForAppAccess() -> biometricsUseCase.authenticate(
                R.string.authenticate,
                R.string.authenticate_for_access,
                R.string.authenticate_for_access_description,
                true,
            )
            else -> Ok(Unit)
        }
        when(proceed) {
            is Err -> emit(HomeMutation.NeedsBiometricAuthentication)
            is Ok -> navigator.newRoot(
                when {
                    welcomeUseCase.needToShowWelcomeScreen() -> WelcomeNavigationRoute
                    notificationsUseCase.needToShowNotificationsOnboardingScreen() -> NotificationsNavigationRoute
                    else -> FeedNavigationRoute
                }
            )
        }
    }
}
