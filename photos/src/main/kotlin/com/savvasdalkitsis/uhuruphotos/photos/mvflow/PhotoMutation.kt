package com.savvasdalkitsis.uhuruphotos.photos.mvflow

import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails

sealed class PhotoMutation {
    object HideUI : PhotoMutation()
    object ShowUI : PhotoMutation()
    object DismissErrorMessage : PhotoMutation()
    data class ShowErrorMessage(val message: String) : PhotoMutation()
    object FinishedLoading : PhotoMutation()
    object Loading : PhotoMutation()
    object ShowInfo : PhotoMutation()
    object HideInfo : PhotoMutation()
    object ShowDeletionConfirmationDialog : PhotoMutation()
    object HideDeletionConfirmationDialog : PhotoMutation()
    object ShowShareIcon : PhotoMutation()
    data class ReceivedUrl(val id: String, val lowResUrl: String, val fullResUrl: String) : PhotoMutation()
    data class ReceivedDetails(val details: PhotoDetails) : PhotoMutation()
}
