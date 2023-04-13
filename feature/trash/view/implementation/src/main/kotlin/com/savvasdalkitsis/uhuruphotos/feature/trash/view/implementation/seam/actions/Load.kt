package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffect
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashMutation
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.map

object Load : TrashAction() {
    context(TrashActionsContext) override fun handle(
        state: TrashState,
        effect: EffectHandler<TrashEffect>
    ) = settingsUseCase.observeBiometricsRequiredForTrashAccess()
        .map { required ->
            TrashMutation.DisplayFingerPrintAction(!required && biometricsUseCase.getBiometrics().isSupported)
        }
}