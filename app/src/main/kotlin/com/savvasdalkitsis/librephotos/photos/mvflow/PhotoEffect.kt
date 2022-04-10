package com.savvasdalkitsis.librephotos.photos.mvflow

sealed class PhotoEffect {
    object HideSystemBars : PhotoEffect()
    object ShowSystemBars : PhotoEffect()
    object NavigateBack : PhotoEffect()
}
