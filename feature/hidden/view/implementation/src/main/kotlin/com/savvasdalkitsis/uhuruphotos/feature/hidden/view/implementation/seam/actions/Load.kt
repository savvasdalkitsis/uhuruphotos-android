/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.effects.HiddenPhotosEffect
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosMutation.DisplayFingerPrintAction
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
