package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxDeletionCategory
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
            LightboxDeletionCategory.REMOTE_TRASHED -> {
                // this just schedules deletion so no need to check result
                mediaUseCase.deleteMediaItem(mediaItem.id)
                Result.success(Unit)
            }
            LightboxDeletionCategory.LOCAL_ONLY -> deleteLocal(mediaItem)
            LightboxDeletionCategory.FULLY_SYNCED ->
                deleteLocal(mediaItem).map {
                    // this just schedules deletion so no need to check result
                    trashRemote(mediaItem)
                }
            LightboxDeletionCategory.REMOTE_ONLY -> {
                // this just schedules deletion so no need to check result
                trashRemote(mediaItem)
                Result.success(Unit)
                TODO("Need to replace item with new local only version")
            }
        }
    }

    context (LightboxActionsContext)
    private fun trashRemote(mediaItem: SingleMediaItemState) {
        mediaUseCase.trashMediaItem(mediaItem.id)
    }

}