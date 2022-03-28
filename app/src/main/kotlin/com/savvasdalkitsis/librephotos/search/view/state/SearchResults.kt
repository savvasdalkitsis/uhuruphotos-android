package com.savvasdalkitsis.librephotos.search.view.state

import com.savvasdalkitsis.librephotos.albums.model.Album

sealed class SearchResults {

    object Idle : SearchResults()
    object Searching : SearchResults()
    data class Found(val albums: List<Album>) : SearchResults() {
        override fun toString() = "Found ${albums.size} albums in search."
    }

}
