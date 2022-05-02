package com.savvasdalkitsis.uhuruphotos.photos.model

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails

data class Photo(
    val id: String,
    val thumbnailUrl: String? = null,
    val fullResUrl: String? = null,
    val fallbackColor: String? = null,
    val isFavourite: Boolean = false,
    val ratio: Float = 1f,
    val isVideo: Boolean = false,
    val selectionMode: SelectionMode = SelectionMode.UNDEFINED,
    val latLng: (Pair<Double, Double>)? = null,
)

enum class SelectionMode {
    UNDEFINED, SELECTED, UNSELECTED
}

val PhotoDetails.latLng get() = gpsLat?.toDoubleOrNull()?.let { lat ->
    gpsLon?.toDoubleOrNull()?.let { lon ->
        LatLng(lat, lon)
    }
}