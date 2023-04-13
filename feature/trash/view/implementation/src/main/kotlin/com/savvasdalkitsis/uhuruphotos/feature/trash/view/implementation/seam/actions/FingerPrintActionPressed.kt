package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffect.NavigateToAppSettings
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashMutation
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object FingerPrintActionPressed : TrashAction() {
    context(TrashActionsContext) override fun handle(
        state: TrashState,
        effect: EffectHandler<TrashEffect>
    ) = flow<TrashMutation> {
        effect.handleEffect(NavigateToAppSettings)
    }
}