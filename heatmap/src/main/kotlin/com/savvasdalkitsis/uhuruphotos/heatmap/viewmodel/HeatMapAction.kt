package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class HeatMapAction {
    data class CameraViewPortChanged(val boundsChecker: (LatLng) -> Boolean) : HeatMapAction()
    data class SelectedPhoto(
        val photo: Photo,
        val center: Offset,
        val scale: Float,
    ) : HeatMapAction()
    object Load : HeatMapAction()
    object BackPressed : HeatMapAction()
}
