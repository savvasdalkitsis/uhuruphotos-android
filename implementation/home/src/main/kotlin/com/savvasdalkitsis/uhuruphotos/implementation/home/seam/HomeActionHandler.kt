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
package com.savvasdalkitsis.uhuruphotos.implementation.home.seam

import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeMutation.NeedsBiometricAuthentication
import com.savvasdalkitsis.uhuruphotos.implementation.home.ui.state.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class HomeActionHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val biometricsUseCase: BiometricsUseCase,
) : ActionHandler<HomeState, HomeEffect, HomeAction, HomeMutation> {
    override fun handleAction(
        state: HomeState,
        action: HomeAction,
        effect: suspend (HomeEffect) -> Unit
    ): Flow<HomeMutation> = when (action) {
        is Load -> flow {
            emit(Loading)
            val proceed = when {
                settingsUseCase.getBiometricsRequiredForAppAccess() -> biometricsUseCase.authenticate(
                    string.authenticate,
                    string.authenticate_for_access,
                    string.authenticate_for_access_description,
                    true,
                )
                else -> Result.success(Unit)
            }
            when {
                proceed.isFailure -> emit(NeedsBiometricAuthentication)
                else -> when (authenticationUseCase.authenticationStatus()) {
                    is Unauthenticated -> effect(LaunchAuthentication)
                    else -> effect(HomeEffect.LoadFeed)
                }
            }
        }
    }
}