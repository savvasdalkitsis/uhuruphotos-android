package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class FullMediaDataLoaded(val mediaItemState: SingleMediaItemState) : LightboxAction() {

    context(LightboxActionsContext) override fun handle(
        state: LightboxState,
        effect: EffectHandler<LightboxEffect>
    ) = flow {
        emit(SetOriginalFileIconState(mediaItemState.id, OriginalFileIconState.HIDDEN))
        if (!(mediaItemState.id is MediaId.Remote && mediaItemState.isVideo)) {
            emit(ShowShareIcon(mediaItemState.id))
            emit(ShowUseAsIcon(mediaItemState.id))
            val metadata = metadataUseCase.extractMetadata(mediaItemState.fullResUrl)
            if (metadata != null) {
                emit(ShowMetadata(mediaItemState.id, metadata))
            }
        }
    }

}