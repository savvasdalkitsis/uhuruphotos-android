package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.FULLY_SYNCED_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.LOCAL_ONLY_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory.REMOTE_ITEM_TRASHED
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler

object TrashMediaItem : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ) = processAndRemovePhoto(state, effect) {
        val mediaItem = state.currentMediaItem
        when (deletionCategory(mediaItem)) {
            REMOTE_ITEM_TRASHED -> {
                // this just schedules deletion so no need to check result
                mediaUseCase.deleteMediaItem(mediaItem.id)
                Result.success(Unit)
            }
            LOCAL_ONLY_ITEM -> deleteLocal(mediaItem)
            FULLY_SYNCED_ITEM -> deleteLocal(mediaItem).map {
                // this just schedules deletion so no need to check result
                trashRemote(mediaItem)
            }
            REMOTE_ITEM -> {
                // this just schedules deletion so no need to check result
                trashRemote(mediaItem)
                Result.success(Unit)
            }
        }
    }

    context (LightboxActionsContext)
    private fun trashRemote(mediaItem: SingleMediaItemState) {
        mediaUseCase.trashMediaItem(mediaItem.id)
    }
}