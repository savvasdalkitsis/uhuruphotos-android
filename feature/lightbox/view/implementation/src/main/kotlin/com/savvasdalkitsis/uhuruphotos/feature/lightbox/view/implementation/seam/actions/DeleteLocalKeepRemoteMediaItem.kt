package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ReplaceMediaItemInSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow

object DeleteLocalKeepRemoteMediaItem : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ): Flow<LightboxMutation> {
        return processPhoto(state, effect,
            process = { deleteLocal(state.currentMediaItem) },
            postProcessAction = {
                val current = state.currentMediaItem
                val remote = current.id.findRemote!!
                with(mediaUseCase) {
                    emit(
                        ReplaceMediaItemInSource(
                            current.id,
                            current.copy(
                                id = remote,
                                fullResUrl = remote.toFullSizeUriFromId(current.isVideo),
                                lowResUrl = remote.toThumbnailUriFromId(current.isVideo),
                                localPath = null,
                                mediaItemSyncState = MediaItemSyncState.REMOTE_ONLY,
                            ),
                        )
                    )
                }
            }
        )
    }
}