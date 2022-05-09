package com.savvasdalkitsis.uhuruphotos.albums.api.model

import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo

data class Album(
    val id: String,
    val photoCount: Int,
    val photos: List<Photo>,
    val date: String,
    val location: String?,
)