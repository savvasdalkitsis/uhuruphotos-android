package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.AskForServerDetails
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.AskForUserCredentials
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState.UserCredentials
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object CheckPersistedServer : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<ServerEffect>
    ) = flow {
        when (serverUseCase.getServerUrl()) {
            null -> emit(AskForServerDetails(null, isValid = false))
            else -> when (authenticationUseCase.authenticationStatus()) {
                is ServerDown, is Offline, is Authenticated -> effect.handleEffect(Close)
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