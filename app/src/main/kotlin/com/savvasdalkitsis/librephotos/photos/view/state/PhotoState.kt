package com.savvasdalkitsis.librephotos.photos.view.state

data class PhotoState(
    val isLoading: Boolean = false,
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    val isUIShowing: Boolean = true,
)