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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation.AskForPermissions
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation.PermissionsGranted
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LocalAlbumActionHandler @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
) : ActionHandler<LocalAlbumState, LocalAlbumEffect, LocalAlbumAction, LocalAlbumMutation> {

    override fun handleAction(
        state: LocalAlbumState,
        action: LocalAlbumAction,
        effect: suspend (LocalAlbumEffect) -> Unit
    ): Flow<LocalAlbumMutation> = when (action) {
        is LocalAlbumAction.Load ->
            localMediaUseCase.observePermissionsState()
                .flatMapLatest { when (it) {
                    is RequiresPermissions -> flowOf(AskForPermissions(it.deniedPermissions))
                    else -> flow {
                        PermissionsGranted
                        localMediaUseCase.refreshLocalMediaFolder(action.albumId)
                    }
                } }
    }

}