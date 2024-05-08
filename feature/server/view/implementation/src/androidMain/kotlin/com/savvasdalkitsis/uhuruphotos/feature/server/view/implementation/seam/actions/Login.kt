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
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.SetLoading
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isHttpUrl
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import kotlinx.coroutines.flow.flow

data class Login(
    val allowUnsecuredServers: Boolean,
    val rememberCredentials: Boolean = false,
) : ServerAction() {
    override fun ServerActionsContext.handle(
        state: ServerState
    ) = flow {
        if (!state.allowLogin) {
            return@flow
        }

        if (state.currentUrl.isValidUrlOrDomain) {
            if (!allowUnsecuredServers && state.currentUrl.isHttpUrl) {
                emit(ShowUnsecureServerConfirmation)
            } else {
                serverUseCase.setServerUrl(state.currentUrl)
                emit(SetLoading(true))
                authenticationLoginUseCase.login(
                    credentials = Credentials(state.username, state.password),
                    rememberCredentials = rememberCredentials,
                ).mapEither(
                    success = { authStatus ->
                        when (authStatus) {
                            AuthStatus.Authenticated -> navigator.navigateUp()
                            else -> {
                                emit(SetLoading(false))
                                toaster.show(strings.error_logging_in)
                            }
                        }
                    },
                    failure = {
                        emit(SetLoading(false))
                        toaster.show(strings.error_logging_in)
                    }
                )
            }
        }
    }

}
