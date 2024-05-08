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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeMutation.SetLoading
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeMutation.SetMissingPermissions
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeMutation.SetUseCases
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.WelcomeMutation.UpdateSaveButton
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.andThen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object Load : WelcomeAction() {
    override fun WelcomeActionsContext.handle(state: WelcomeState): Flow<Mutation<WelcomeState>> = merge(
        flowOf(SetLoading(true)),
        welcomeUseCase.observeWelcomeStatus().map { status ->
            if (!status.hasLocalAccess) {
                localMediaUseCase.markLocalMediaSyncedBefore(false)
                localMediaUseCase.doNotScanOtherFolders()
            }
            SetUseCases(
                localMediaSelected = status.hasLocalAccess,
                cloudMediaSelected = status.hasRemoteAccess,
            ) andThen SetLoading(false) andThen UpdateSaveButton andThen SetMissingPermissions(
                (status.permissions as? RequiresPermissions)?.deniedPermissions
            )
        }
    )
}