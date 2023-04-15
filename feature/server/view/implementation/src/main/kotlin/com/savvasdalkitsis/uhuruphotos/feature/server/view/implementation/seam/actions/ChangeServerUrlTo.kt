package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class ChangeServerUrlTo(val url: String) : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<ServerEffect>
    ) = flow {
        emit(HideUnsecureServerConfirmation)
        if (url.isValidUrlOrDomain) {
            serverUseCase.setServerUrl(url)
            effect.handleEffect(Close)
        }
    }

}