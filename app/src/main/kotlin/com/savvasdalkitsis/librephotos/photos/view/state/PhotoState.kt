package com.savvasdalkitsis.librephotos.photos.view.state

data class PhotoState(
    val id: String = "",
    val isLoading: Boolean = false,
    val fullResUrl: String = "",
    val lowResUrl: String = "",
    val errorMessage: String? = null,
    val showUI: Boolean = true,
    val showRefresh: Boolean = false,
    val isFavourite: Boolean? = null,
)