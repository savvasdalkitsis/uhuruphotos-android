package com.savvasdalkitsis.librephotos.photos.mvflow

sealed class PhotoAction {
    data class LoadPhoto(val id: String) : PhotoAction()
}
