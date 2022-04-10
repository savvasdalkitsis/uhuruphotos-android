package com.savvasdalkitsis.librephotos.photos.mvflow

import com.savvasdalkitsis.librephotos.photos.db.PhotoDetails

sealed class PhotoMutation {
    object HideUI : PhotoMutation()
    object ShowUI : PhotoMutation()
    data class ReceivedUrl(val lowResUrl: String, val fullResUrl: String) : PhotoMutation()
    data class ReceivedDetails(val details: PhotoDetails) : PhotoMutation()
}
