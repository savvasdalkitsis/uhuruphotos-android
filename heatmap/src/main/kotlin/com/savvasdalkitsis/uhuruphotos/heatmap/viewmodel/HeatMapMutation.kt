package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class HeatMapMutation {
    data class UpdateAllPhotos(val photos: List<Photo>) : HeatMapMutation()
    data class UpdateDisplay(
        val photosToDisplay: List<Photo>,
        val pointsToDisplay: List<LatLng>,
    ) : HeatMapMutation()
    data class ShowLoading(val loading: Boolean) : HeatMapMutation()
}
