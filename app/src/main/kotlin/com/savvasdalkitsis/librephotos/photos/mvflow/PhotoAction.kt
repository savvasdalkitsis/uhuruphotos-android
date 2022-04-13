package com.savvasdalkitsis.librephotos.photos.mvflow

import com.google.android.gms.maps.model.LatLng

sealed class PhotoAction {
    object ToggleUI : PhotoAction()
    object NavigateBack : PhotoAction()
    object Refresh : PhotoAction()
    object DismissErrorMessage : PhotoAction()
    object ShowInfo : PhotoAction()
    object HideInfo : PhotoAction()
    data class ClickedOnMap(val gps: LatLng) : PhotoAction()
    data class LoadPhoto(val id: String) : PhotoAction()
    data class SetFavourite(val favourite: Boolean) : PhotoAction()
}
