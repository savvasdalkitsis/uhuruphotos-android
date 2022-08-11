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
package com.savvasdalkitsis.uhuruphotos.implementation.server.seam

import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.http.isHttpUrl
import com.savvasdalkitsis.uhuruphotos.api.http.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.AttemptChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.DismissUnsecuredServerDialog
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.Login
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.SendLogsClick
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.SetLoggingEnabled
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.TogglePasswordVisibility
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.UrlTyped
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.UserPasswordChangedTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerAction.UsernameChangedTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.SendFeedback
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.ChangePasswordTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.ChangeUsernameTo
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.PerformingBackgroundJob
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.SetPasswordVisibility
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.implementation.server.view.ServerState.UserCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ServerActionHandler @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ActionHandler<ServerState, ServerEffect, ServerAction, ServerMutation> {

    override fun handleAction(
        state: ServerState,
        action: ServerAction,
        effect: suspend (ServerEffect) -> Unit,
    ): Flow<ServerMutation> = when (action) {
        Load -> settingsUseCase.observeLoggingEnabled()
            .map(ServerMutation::SetLoggingEnabled)
        CheckPersistedServer -> flow {
            when (serverUseCase.getServerUrl()) {
                null -> emit(AskForServerDetails(null, isValid = false))
                else -> when (authenticationUseCase.authenticationStatus()) {
                    is ServerDown, is Offline, is Authenticated -> effect(Close)
                    is Unauthenticated -> {
                        when (state) {
                            is UserCredentials -> emit(AskForUserCredentials(state.username, state.password))
                            else -> emit(AskForUserCredentials("", ""))
                        }
                    }
                }
            }
        }
        is RequestServerUrlChange -> flow {
            val prefilledUrl = serverUseCase.getServerUrl()
            emit(AskForServerDetails(prefilledUrl, prefilledUrl?.isValidUrlOrDomain == true))
        }
        is UrlTyped -> flow {
            val prefilledUrl = serverUseCase.getServerUrl()
            emit(ShowUrlValidation(prefilledUrl, action.url.isValidUrlOrDomain))
        }
        is ChangeServerUrlTo -> flow {
            emit(HideUnsecureServerConfirmation)
            if (action.url.isValidUrlOrDomain) {
                serverUseCase.setServerUrl(action.url)
                effect(Close)
            }
        }
        is AttemptChangeServerUrlTo -> flow {
            if (action.url.isValidUrlOrDomain) {
                if (action.url.isHttpUrl) {
                    emit(ShowUnsecureServerConfirmation)
                } else {
                    serverUseCase.setServerUrl(action.url)
                    effect(Close)
                }
            }
        }
        DismissUnsecuredServerDialog -> flowOf(HideUnsecureServerConfirmation)
        Login -> flow {
            if ((state as? UserCredentials)?.allowLogin == false) {
                return@flow
            }
            emit(PerformingBackgroundJob)
            val credentials = state as UserCredentials
            authenticationUseCase.login(credentials.username, credentials.password)
                .onSuccess { authStatus ->
                    if (authStatus == Authenticated) {
                        effect(Close)
                    } else {
                        effect(ErrorLoggingIn())
                        emit(AskForUserCredentials(credentials.username, credentials.password))
                    }
                }
                .onFailure {
                    effect(ErrorLoggingIn(it))
                    emit(AskForUserCredentials(credentials.username, credentials.password))
                }
        }
        is UsernameChangedTo -> flowOf(ChangeUsernameTo(action.username.lowercase()))
        is UserPasswordChangedTo -> flowOf(ChangePasswordTo(action.password))
        SendLogsClick -> flow {
            effect(SendFeedback)
        }
        TogglePasswordVisibility -> flowOf(SetPasswordVisibility(!(state as UserCredentials).passwordVisible))
        is SetLoggingEnabled -> flow {
            settingsUseCase.setLoggingEnabled(action.enabled)
        }
    }

}
