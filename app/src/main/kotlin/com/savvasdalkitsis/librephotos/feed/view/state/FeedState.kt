package com.savvasdalkitsis.librephotos.feed.view.state

import com.savvasdalkitsis.librephotos.albums.model.Album

data class FeedState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val feedDisplay: FeedDisplay = FeedDisplay.FULL,
) {
    override fun toString() = "Feed with ${albums.size} albums."
}