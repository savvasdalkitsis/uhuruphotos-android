package com.savvasdalkitsis.librephotos.albums.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumsByDate(
    val count: Int,
    val results: List<Album.IncompleteAlbum>,
)