package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class DownloadOriginal(val mediaItemState: SingleMediaItemState) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ) = flow<LightboxMutation> {
        mediaUseCase.downloadOriginal(mediaItemState.id, mediaItemState.isVideo)
        effect.handleEffect(LightboxEffect.DownloadingOriginal)
    }
}