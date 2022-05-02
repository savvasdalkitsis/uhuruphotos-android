package com.savvasdalkitsis.uhuruphotos.heatmap.view.state

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

data class HeatMapState(
    val loading: Boolean = false,
    val pointsToDisplay: List<LatLng> = emptyList(),
    val allPhotos: List<Photo> = emptyList(),
    val photosToDisplay: List<Photo> = emptyList(),
)
