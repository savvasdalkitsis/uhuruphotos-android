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

import android.os.Build
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.FULLY_SYNCED_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.LOCAL_ONLY_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM_TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowFullySyncedDeleteConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowRemoteTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.effects.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

data object AskForMediaItemTrashing : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ) = flow {
        when (deletionCategory(state.currentMediaItem)) {
            REMOTE_ITEM_TRASHED -> emit(ShowDeleteConfirmationDialog)
            FULLY_SYNCED_ITEM -> emit(ShowFullySyncedDeleteConfirmationDialog)
            LOCAL_ONLY_ITEM -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // On R and later, we have to present the user with a native dialog so no need to show
                // ours for local only items
                emitAll(processAndRemoveMediaItem(state, effect) {
                    deleteLocal(state.currentMediaItem)
                })
            } else {
                emit(ShowDeleteConfirmationDialog)
            }

            REMOTE_ITEM -> emit(ShowRemoteTrashingConfirmationDialog)
        }
    }
}
