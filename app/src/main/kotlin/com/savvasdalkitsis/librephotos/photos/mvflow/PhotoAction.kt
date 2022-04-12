package com.savvasdalkitsis.librephotos.photos.mvflow

sealed class PhotoAction {
    object ToggleUI : PhotoAction()
    object NavigateBack : PhotoAction()
    object Refresh : PhotoAction()
    data class LoadPhoto(val id: String) : PhotoAction()
    data class SetFavourite(val favourite: Boolean) : PhotoAction()
}
