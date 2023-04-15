package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isHttpUrl
import com.savvasdalkitsis.uhuruphotos.foundation.http.api.isValidUrlOrDomain
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class AttemptChangeServerUrlTo(val url: String) : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<ServerEffect>
    ) = flow {
        if (url.isValidUrlOrDomain) {
            if (url.isHttpUrl) {
                emit(ShowUnsecureServerConfirmation)
            } else {
                serverUseCase.setServerUrl(url)
                effect.handleEffect(Close)
            }
        }
    }

}