package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object Login : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<ServerEffect>
    ) = flow {
        if ((state as? ServerState.UserCredentials)?.allowLogin == false) {
            return@flow
        }
        emit(PerformingBackgroundJob)
        val credentials = state as ServerState.UserCredentials
        authenticationUseCase.login(credentials.username, credentials.password)
            .onSuccess { authStatus ->
                if (authStatus == AuthStatus.Authenticated) {
                    effect.handleEffect(Close)
                } else {
                    effect.handleEffect(ErrorLoggingIn())
                    emit(AskForUserCredentials(credentials.username, credentials.password))
                }
            }
            .onFailure {
                effect.handleEffect(ErrorLoggingIn(it))
                emit(AskForUserCredentials(credentials.username, credentials.password))
            }
    }

}