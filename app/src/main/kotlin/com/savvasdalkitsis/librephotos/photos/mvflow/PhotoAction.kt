package com.savvasdalkitsis.librephotos.photos.mvflow

sealed class PhotoAction {
    object ToggleUI : PhotoAction()
    object NavigateBack : PhotoAction()
    data class LoadPhoto(val id: String) : PhotoAction()
}
