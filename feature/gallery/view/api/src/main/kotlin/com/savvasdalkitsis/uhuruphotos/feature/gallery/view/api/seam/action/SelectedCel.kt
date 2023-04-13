package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class SelectedCel(
    val cel: CelState,
    val center: Offset,
    val scale: Float,
) : GalleryAction() {
    context(GalleryActionsContext) override fun handle(
        state: GalleryState,
        effect: EffectHandler<GalleryEffect>
    ) = flow<GalleryMutation> {
        effect.handleEffect(
            OpenLightbox(
                id = cel.mediaItem.id,
                center = center,
                scale = scale,
                video = cel.mediaItem.isVideo,
                lightboxSequenceDataSource = lightboxSequenceDataSource(galleryId)
            )
        )
    }
}