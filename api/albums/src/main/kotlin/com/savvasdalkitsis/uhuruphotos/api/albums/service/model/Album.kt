/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.api.albums.service.model

import com.savvasdalkitsis.uhuruphotos.api.db.albums.Albums
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSummaryItem
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