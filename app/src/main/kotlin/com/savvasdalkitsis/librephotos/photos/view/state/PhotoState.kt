package com.savvasdalkitsis.librephotos.photos.view.state

data class PhotoState(
    val id: String = "",
    val isLoading: Boolean = false,
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    val isUIShowing: Boolean = true,
    val isFavourite: Boolean? = null,
)