package com.savvasdalkitsis.uhuruphotos.api.albums.ui.state

enum class AlbumSorting {
    DATE_DESC, DATE_ASC, ALPHABETICAL_ASC, ALPHABETICAL_DESC;

    companion object {
        val default = DATE_DESC

        fun <T> List<T>.sorted(
            sorting: AlbumSorting,
            timeStamp: (T) -> String?,
            title: (T) -> String?,
        ): List<T> = sortedBy {
            when (sorting) {
                DATE_DESC, DATE_ASC -> timeStamp(it)
                ALPHABETICAL_ASC, ALPHABETICAL_DESC -> title(it)
            }
        }.let {
            when (sorting) {
                DATE_ASC, ALPHABETICAL_ASC -> it
                DATE_DESC, ALPHABETICAL_DESC -> it.reversed()
            }
        }
    }
}