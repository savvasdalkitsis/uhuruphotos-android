package com.savvasdalkitsis.uhuruphotos.search.view.state

import com.savvasdalkitsis.uhuruphotos.albums.model.Album

sealed class SearchResults {

    object Idle : SearchResults()
    object Searching : SearchResults()
    data class Found(val albums: List<Album>) : SearchResults() {
        override fun toString() = "Found ${albums.size} albums in search."
    }

}
