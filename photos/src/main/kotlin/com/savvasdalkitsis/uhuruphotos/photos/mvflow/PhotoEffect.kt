package com.savvasdalkitsis.uhuruphotos.photos.mvflow

import com.google.android.gms.maps.model.LatLng

sealed class PhotoEffect {
    data class LaunchMap(val gps: LatLng) : PhotoEffect()
    data class CopyToClipboard(val content: String) : PhotoEffect()
    object HideSystemBars : PhotoEffect()
    object ShowSystemBars : PhotoEffect()
    object NavigateBack : PhotoEffect()
    object ErrorRefreshingPeople : PhotoEffect()

    data class SharePhoto(val url: String) : PhotoEffect()
    data class NavigateToPerson(val id: Int) : PhotoEffect()
}
