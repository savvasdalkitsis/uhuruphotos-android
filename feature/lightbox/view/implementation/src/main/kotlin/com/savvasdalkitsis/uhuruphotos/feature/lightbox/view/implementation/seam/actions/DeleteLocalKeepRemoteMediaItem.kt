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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReplaceMediaItemInSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncStateModel
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.Flow

data object DeleteLocalKeepRemoteMediaItem : LightboxAction() {

    override fun LightboxActionsContext.handle(
        state: LightboxState
    ): Flow<LightboxMutation> {
        return processMediaItem(state, process = { deleteLocal(state.currentMediaItem) }
        ) {
            val current = state.currentMediaItem
            val remote = current.id.findRemote!!
            emit(
                ReplaceMediaItemInSource(
                    current.id,
                    current.copy(
                        id = remote,
                        details = current.details.copy(
                            localPaths = persistentSetOf(),
                        ),
                        mediaItemSyncState = MediaItemSyncStateModel.REMOTE_ONLY,
                    ),
                )
            )
        }
    }
}
