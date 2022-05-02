package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class HeatMapEffect {
    data class NavigateToPhoto(
        val photo: Photo,
        val center: Offset,
        val scale: Float,
    ) : HeatMapEffect()
    object ErrorLoadingPhotoDetails : HeatMapEffect()
    object NavigateBack : HeatMapEffect()

}
