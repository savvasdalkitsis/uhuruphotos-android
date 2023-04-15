package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.*
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flowOf

object AskForMediaItemTrashing : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ) = flowOf(when (deletionCategory(state.currentMediaItem)) {
        REMOTE_ITEM_TRASHED -> ShowDeleteConfirmationDialog
        FULLY_SYNCED_ITEM -> ShowFullySyncedDeleteConfirmationDialog
        LOCAL_ONLY_ITEM -> ShowDeleteConfirmationDialog
        REMOTE_ITEM -> ShowRemoteTrashingConfirmationDialog
    })

}