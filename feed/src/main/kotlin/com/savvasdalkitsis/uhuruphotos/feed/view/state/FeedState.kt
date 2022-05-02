package com.savvasdalkitsis.uhuruphotos.feed.view.state

import com.savvasdalkitsis.uhuruphotos.albums.model.Album

data class FeedState(
    val isLoading: Boolean = true,
    val albums: List<Album> = emptyList(),
    val feedDisplay: FeedDisplay = FeedDisplays.default,
) {
    override fun toString() = "Feed with ${albums.size} albums."
}