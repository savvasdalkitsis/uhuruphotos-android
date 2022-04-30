package com.savvasdalkitsis.uhuruphotos.albums.service.model

import com.savvasdalkitsis.uhuruphotos.db.albums.Albums
import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoSummaryItem
import com.squareup.moshi.JsonClass

sealed class Album(
    open val id: String,
    open val date: String?,
    open val location: String,
    open val incomplete: Boolean,
    open val numberOfItems: Int,
) {

    @JsonClass(generateAdapter = true)
    data class IncompleteAlbum(
        override val id: String,
        override val date: String?,
        override val location: String,
        override val incomplete: Boolean,
        override val numberOfItems: Int,
    ) : Album(id, date, location, incomplete, numberOfItems)

    @JsonClass(generateAdapter = true)
    data class CompleteAlbum(
        override val id: String,
        override val date: String?,
        override val location: String,
        override val incomplete: Boolean,
        override val numberOfItems: Int,
        val rating: Int?,
        val items: List<PhotoSummaryItem>,
    ) : Album(id, date, location, incomplete, numberOfItems)
}

fun Album.IncompleteAlbum.toAlbum() = Albums(
    id,
    date,
    location,
    rating = null,
    incomplete,
    numberOfItems,
)