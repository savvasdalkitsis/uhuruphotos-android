package com.savvasdalkitsis.uhuruphotos.people.view.state

enum class SortOrder {
    ASCENDING, DESCENDING;

    fun toggle(): SortOrder = when (this) {
        ASCENDING -> DESCENDING
        DESCENDING -> ASCENDING
    }

    companion object {
        val default = ASCENDING
    }
}
