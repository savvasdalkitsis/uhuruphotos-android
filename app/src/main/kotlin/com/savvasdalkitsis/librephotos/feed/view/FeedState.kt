package com.savvasdalkitsis.librephotos.feed.view

import com.savvasdalkitsis.librephotos.albums.model.Album

data class FeedState(
    val albums: List<Album> = emptyList(),
) {
    override fun toString() = "Feed with ${albums.size} albums."
}