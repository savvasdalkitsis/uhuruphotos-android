package com.savvasdalkitsis.librephotos.search.mvflow

sealed class SearchMutation {

    object SearchCleared : SearchMutation()
    data class QueryChanged(val query: String) : SearchMutation()
    data class FocusChanged(val focused: Boolean) : SearchMutation()
}