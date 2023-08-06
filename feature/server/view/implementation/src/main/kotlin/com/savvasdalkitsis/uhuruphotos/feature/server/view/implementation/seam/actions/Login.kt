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

import com.github.michaelbull.result.mapEither
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.Credentials
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.PerformingBackgroundJob
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.ClearBackStack
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.CommonEffect
import com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects.ShowToast
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

data object Login : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<CommonEffect>
    ) = flow {
        if ((state as? ServerState.UserCredentials)?.allowLogin == false) {
            return@flow
        }
        emit(PerformingBackgroundJob)
        val credentials = state as ServerState.UserCredentials
        authenticationLoginUseCase.login(Credentials(credentials.username, credentials.password))
            .mapEither(
                success = { authStatus ->
                    if (authStatus == AuthStatus.Authenticated) {
                        effect.handleEffect(ClearBackStack)
                    } else {
                        effect.handleEffect(ShowToast(R.string.error_logging_in))
                        emit(AskForUserCredentials(credentials.username, credentials.password))
                    }
                },
                failure = {
                    effect.handleEffect(ShowToast(R.string.error_logging_in))
                    emit(AskForUserCredentials(credentials.username, credentials.password))
                }
            )
    }

}
