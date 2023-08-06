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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState.UserCredentials
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.ClearBackStack
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data object CheckPersistedServer : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<CommonEffect>
    ) = flow {
        when (serverUseCase.getServerUrl()) {
            null -> emit(AskForServerDetails(null, isValid = false))
            else -> when (authenticationUseCase.authenticationStatus()) {
                is ServerDown, is Offline, is Authenticated -> effect.handleEffect(ClearBackStack)
                is Unauthenticated -> emit(
                    when (state) {
                        is UserCredentials -> AskForUserCredentials(state.username, state.password)
                        else -> AskForUserCredentials("", "")
                    }
                )
            }
        }
    }
}
