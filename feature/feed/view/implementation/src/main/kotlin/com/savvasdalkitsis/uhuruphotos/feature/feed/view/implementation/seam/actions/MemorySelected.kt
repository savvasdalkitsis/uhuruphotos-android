package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenMemoryLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class MemorySelected(val memoryCel: CelState, val center: Offset, val scale: Float) : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow<FeedMutation> {
        effect.handleEffect(
            OpenMemoryLightbox(
                id = memoryCel.mediaItem.id,
                center = center,
                scale = scale,
                isVideo = memoryCel.mediaItem.isVideo,
            )
        )
    }
}