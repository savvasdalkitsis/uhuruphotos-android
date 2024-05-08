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
package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashMutation
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import kotlinx.coroutines.flow.map

data object Load : TrashAction() {
    override fun TrashActionsContext.handle(
        state: TrashState
    ) = settingsUseCase.observeBiometricsRequiredForTrashAccess()
        .map { required ->
            TrashMutation.DisplayFingerPrintAction(!required && biometricsUseCase.getBiometrics().isSupported)
        }
}
