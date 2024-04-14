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

import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ChangePasswordTo
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ChangeUsernameTo
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.SetRememberCredentials
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.andThen
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object Load : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState
    ) = merge(
        serverUseCase.observeServerUrl()
            .map { ChangeServerUrlTo(it) andThen ShowUrlValidation(it, it.isValidUrlOrDomain) },
        settingsUseCase.observeLoggingEnabled()
            .map(ServerMutation::SetLoggingEnabled),
        flow {
            authenticationLoginUseCase.loadSavedCredentials()?.let { credentials ->
                if (!credentials.isEmpty) {
                    emit(ChangeUsernameTo(credentials.username))
                    emit(ChangePasswordTo(credentials.password))
                    emit(SetRememberCredentials(true))
                }
            }
        }
    )
}
