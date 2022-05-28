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
package com.savvasdalkitsis.uhuruphotos.server.viewmodel

import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.isHttpUrl
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.AttemptChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.DismissUnsecuredServerDialog
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.Login
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.RequestServerUrlChange
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.SendLogsClick
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.TogglePasswordVisibility
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.UrlTyped
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.UserPasswordChangedTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerAction.UsernameChangedTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerEffect.SendFeedback
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ChangePasswordTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ChangeUsernameTo
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.PerformingBackgroundJob
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.SetPasswordVisibility
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.server.mvflow.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState
import com.savvasdalkitsis.uhuruphotos.server.view.ServerState.UserCredentials
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ServerHandler @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : Handler<ServerState, ServerEffect, ServerAction, ServerMutation> {

    override fun invoke(
        state: ServerState,
        action: ServerAction,
        effect: suspend (ServerEffect) -> Unit,
    ): Flow<ServerMutation> = when (action) {
        CheckPersistedServer -> flow {
            when (serverUseCase.getServerUrl()) {
                null -> emit(AskForServerDetails(null, isValid = false))
                else -> when (authenticationUseCase.authenticationStatus()) {
                    is Offline, is Authenticated -> effect(Close)
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
            try {
                authenticationUseCase.login(credentials.username, credentials.password)
                effect(Close)
            } catch (e: Exception) {
                log(e)
                effect(ErrorLoggingIn(e))
                emit(AskForUserCredentials(credentials.username, credentials.password))
            }
        }
        is UsernameChangedTo -> flowOf(ChangeUsernameTo(action.username.lowercase()))
        is UserPasswordChangedTo -> flowOf(ChangePasswordTo(action.password))
        SendLogsClick -> flow {
            effect(SendFeedback)
        }
        TogglePasswordVisibility -> flowOf(SetPasswordVisibility(!(state as UserCredentials).passwordVisible))
    }

}
