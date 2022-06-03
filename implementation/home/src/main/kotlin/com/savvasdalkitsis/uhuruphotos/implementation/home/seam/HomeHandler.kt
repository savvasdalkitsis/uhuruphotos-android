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

import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.home.seam.HomeMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.implementation.home.view.state.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ActionHandler<HomeState, HomeEffect, HomeAction, HomeMutation> {
    override fun handleAction(
        state: HomeState,
        action: HomeAction,
        effect: suspend (HomeEffect) -> Unit
    ): Flow<HomeMutation> = when (action) {
        is Load -> flow {
            emit(Loading)
            emit(ShowLibrary(settingsUseCase.getShowLibrary()))
            when (authenticationUseCase.authenticationStatus()) {
                is Unauthenticated -> effect(LaunchAuthentication)
                else -> effect(HomeEffect.LoadFeed)
            }
        }
    }
}