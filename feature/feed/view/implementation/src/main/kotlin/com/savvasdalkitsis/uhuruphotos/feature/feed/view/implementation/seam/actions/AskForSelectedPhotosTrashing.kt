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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import android.os.Build
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowFullySyncedDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.effects.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.syncStates
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequest
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data object AskForSelectedPhotosTrashing : FeedAction() {

    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow {
        when (state.selectedCels.syncStates) {
            setOf(REMOTE_ONLY) -> emit(ShowTrashingConfirmationDialog)
            setOf(LOCAL_ONLY) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // On R and later, we have to present the user with a native dialog so no need to show
                    // ours for local only items
                    localMediaDeletionUseCase.deleteLocalMediaItems(state.selectedCels.map {
                        val id = it.mediaItem.id.findLocal!!
                        LocalMediaDeletionRequest(id.value, id.isVideo)
                    })
                } else {
                    emit(ShowDeleteConfirmationDialog)
                }
            }
            setOf(SYNCED) -> emit(ShowFullySyncedDeleteConfirmationDialog)
        }
    }
}
