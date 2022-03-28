package com.savvasdalkitsis.librephotos.search.mvflow

import com.savvasdalkitsis.librephotos.albums.model.Album

sealed class SearchMutation {

    object SearchCleared : SearchMutation()
    object SearchStarted : SearchMutation()
    data class QueryChanged(val query: String) : SearchMutation()
    data class FocusChanged(val focused: Boolean) : SearchMutation()
    data class SearchResultsUpdated(val albums: List<Album>) : SearchMutation() {
        override fun toString() = "Updating results with ${albums.size} albums"
    }
}