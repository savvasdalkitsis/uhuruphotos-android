package com.savvasdalkitsis.librephotos.server.viewmodel

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.CheckPersistedServer
import com.savvasdalkitsis.librephotos.server.mvflow.ServerAction.UrlChangedTo
import com.savvasdalkitsis.librephotos.server.mvflow.ServerEffect
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation
import com.savvasdalkitsis.librephotos.server.mvflow.ServerMutation.ChangeUrlTo
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.server.viewmodel.state.ServerState
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
                null -> emit(ServerMutation.AskForServerDetails(null))
                else -> when (authenticationUseCase.authenticationStatus().first()) {
                    is AuthStatus.Authenticated -> effect.send(ServerEffect.Close)
                    is AuthStatus.Unauthenticated -> emit(ServerMutation.AskForUserCredentials)
                }
            }
        }
        is UrlChangedTo -> flowOf(ChangeUrlTo(action.url))
        ServerAction.ServerUrlChange -> flow {
            serverUseCase.setServerUrl(state.serverUrl.orEmpty())
            effect.send(ServerEffect.Close)
        }
    }

}
