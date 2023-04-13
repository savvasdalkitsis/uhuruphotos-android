package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class SelectedCel(
    val celState: CelState,
    val center: Offset,
    val scale: Float,
) : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow<FeedMutation> {
        when {
            state.selectedCelCount == 0 -> effect.handleEffect(
                OpenLightbox(celState.mediaItem.id, center, scale, celState.mediaItem.isVideo)
            )
            celState.selectionMode == MediaItemSelectionMode.SELECTED -> {
                effect.handleEffect(FeedEffect.Vibrate)
                celState.deselect()
            }
            else -> {
                effect.handleEffect(FeedEffect.Vibrate)
                celState.select()
            }
        }
    }

}