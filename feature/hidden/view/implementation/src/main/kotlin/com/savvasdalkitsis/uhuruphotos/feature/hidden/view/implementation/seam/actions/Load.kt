package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosEffect
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.map

object Load : HiddenPhotosAction() {
    context(HiddenPhotosActionsContext) override fun handle(
        state: HiddenPhotosState,
        effect: EffectHandler<HiddenPhotosEffect>
    ) = settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess()
        .map { required ->
            DisplayFingerPrintAction(!required && biometricsUseCase.getBiometrics().isSupported)
        }
}