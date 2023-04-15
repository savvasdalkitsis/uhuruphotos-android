package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerEffect
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState.*
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flowOf

object TogglePasswordVisibility : ServerAction() {
    context(ServerActionsContext) override fun handle(
        state: ServerState,
        effect: EffectHandler<ServerEffect>
    ) = flowOf(SetPasswordVisibility(!(state as UserCredentials).passwordVisible))
}