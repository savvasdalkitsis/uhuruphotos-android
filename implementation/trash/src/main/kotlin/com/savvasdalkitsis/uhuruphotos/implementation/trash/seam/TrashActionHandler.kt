/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.implementation.trash.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.biometrics.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashAction.*
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashEffect.NavigateToAppSettings
import com.savvasdalkitsis.uhuruphotos.implementation.trash.seam.TrashMutation.*
import com.savvasdalkitsis.uhuruphotos.implementation.trash.view.state.TrashState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrashActionHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val biometricsUseCase: BiometricsUseCase,
): ActionHandler<TrashState, TrashEffect, TrashAction, TrashMutation> {

    override fun handleAction(
        state: TrashState,
        action: TrashAction,
        effect: suspend (TrashEffect) -> Unit
    ): Flow<TrashMutation> = when (action) {
        Load -> settingsUseCase.observeBiometricsRequiredForTrashAccess()
            .map { required ->
                DisplayFingerPrintAction(!required && biometricsUseCase.getBiometrics().isSupported)
            }
        FingerPrintActionPressed -> flow {
            effect(NavigateToAppSettings)
        }
    }

}
