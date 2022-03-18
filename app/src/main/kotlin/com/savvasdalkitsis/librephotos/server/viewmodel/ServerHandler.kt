package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.*
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.Close
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation.*
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState.UserCredentials
import kotlinx.coroutines.flow.*
import net.pedroloureiro.mvflow.EffectSender
import net.pedroloureiro.mvflow.HandlerWithEffects
import javax.inject.Inject

class ServerHandler @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : HandlerWithEffects<ServerState, ServerAction, ServerMutation, ServerEffect> {

    override fun invoke(
        state: ServerState,
        action: ServerAction,
        effect: EffectSender<ServerEffect>
    ): Flow<ServerMutation> = when (action) {
        CheckPersistedServer -> flow {
            when (serverUseCase.getServerUrl()) {
                null -> emit(AskForServerDetails(null))
                else -> when (authenticationUseCase.authenticationStatus().first()) {
                    is AuthStatus.Authenticated -> effect.send(Close)
                    is AuthStatus.Unauthenticated -> emit(AskForUserCredentials("", ""))
                }
            }
        }
        is RequestServerUrlChange -> flow {
            emit(AskForServerDetails(serverUseCase.getServerUrl()))
        }
        is UrlTyped -> flowOf(ChangeUrlTo(action.url))
        is ChangeServerUrlTo -> flow {
            serverUseCase.setServerUrl(action.url)
            effect.send(Close)
        }
        Login -> flow {
            emit(PerformingBackgroundJob)
            val credentials = state as UserCredentials
            try {
                authenticationUseCase.login(credentials.userEmail, credentials.password)
                effect.send(Close)
            } catch (e: Exception) {
                emit(AskForUserCredentials(credentials.userEmail, credentials.password))
                effect.send(ErrorLoggingIn(e))
            }
        }
        is UserEmailChangedTo -> flowOf(ChangeUserEmailTo(action.userEmail))
        is UserPasswordChangedTo -> flowOf(ChangePasswordTo(action.password))
    }

}
