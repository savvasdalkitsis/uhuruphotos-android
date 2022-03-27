package com.savvasdalkitsis.librephotos.albums.model

import com.savvasdalkitsis.librephotos.photos.model.Photo

data class Album(
    val photoCount: Int,
    val photos: List<Photo>,
)