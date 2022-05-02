package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapMutation.*
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun heatMapReducer(): Reducer<HeatMapState, HeatMapMutation> = { state, mutation ->
    when (mutation) {
        is UpdateAllPhotos -> state.copy(
            pointsToDisplay = mutation.photos
                .mapNotNull { it.latLng }
                .map { (lat, lon) -> LatLng(lat, lon) },
            allPhotos = mutation.photos,
        )
        is ShowLoading -> state.copy(loading = mutation.loading)
        is UpdateDisplay -> state.copy(
            photosToDisplay = mutation.photosToDisplay,
            pointsToDisplay = mutation.pointsToDisplay,
        )
    }
}