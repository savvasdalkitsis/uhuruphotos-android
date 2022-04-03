package com.savvasdalkitsis.librephotos.server.viewmodel

import com.orhanobut.logger.Logger
import com.orhanobut.logger.Logger.VERBOSE
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.extensions.isValidUrl
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.*
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation.*
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.server.view.ServerState
import com.savvasdalkitsis.librephotos.server.view.ServerState.UserCredentials
import com.savvasdalkitsis.librephotos.viewmodel.Handler
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
                    is AuthStatus.Authenticated -> effect(Close)
                    is AuthStatus.Unauthenticated -> emit(AskForUserCredentials("", ""))
                }
            }
        }
        is RequestServerUrlChange -> flow {
            val previousUrl = serverUseCase.getServerUrl()
            emit(AskForServerDetails(previousUrl, previousUrl?.isValidUrl == true))
        }
        is UrlTyped -> flow {
            emit(ChangeUrlTo(action.url, action.url.isValidUrl))
        }
        is ChangeServerUrlTo -> flow {
            if (action.url.isValidUrl) {
                serverUseCase.setServerUrl(action.url)
                effect(Close)
            }
        }
        Login -> flow {
            emit(PerformingBackgroundJob)
            val credentials = state as UserCredentials
            try {
                authenticationUseCase.login(credentials.username, credentials.password)
                effect(Close)
            } catch (e: Exception) {
                Logger.log(VERBOSE, "ServerHandler", e.message, e)
                effect(ErrorLoggingIn(e))
                emit(AskForUserCredentials(credentials.username, credentials.password))
            }
        }
        is UsernameChangedTo -> flowOf(ChangeUsernameTo(action.username))
        is UserPasswordChangedTo -> flowOf(ChangePasswordTo(action.password))
    }

}
