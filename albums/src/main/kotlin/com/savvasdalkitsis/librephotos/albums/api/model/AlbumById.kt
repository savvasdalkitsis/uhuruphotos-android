package com.savvasdalkitsis.librephotos.albums.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumById(
    val results: Album.CompleteAlbum,
)