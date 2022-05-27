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
package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction.Load
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeHandler @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val settingsUseCase: SettingsUseCase,
) : Handler<HomeState, HomeEffect, HomeAction, HomeMutation> {

    override fun invoke(
        state: HomeState,
        action: HomeAction,
        effect: suspend (HomeEffect) -> Unit,
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
