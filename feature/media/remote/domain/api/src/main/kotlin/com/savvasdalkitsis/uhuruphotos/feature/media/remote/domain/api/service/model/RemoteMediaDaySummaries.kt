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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummary
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class RemoteMediaDaySummaries(
    open val id: String,
    open val date: String?,
    open val location: String,
    open val incomplete: Boolean,
    open val numberOfItems: Int,
) {

    @JsonClass(generateAdapter = true)
    data class Incomplete(
        @field:Json(name = "id")
        override val id: String,
        @field:Json(name = "date")
        override val date: String?,
        @field:Json(name = "location")
        override val location: String,
        @field:Json(name = "incomplete")
        override val incomplete: Boolean,
        @field:Json(name = "numberOfItems")
        override val numberOfItems: Int,
    ) : RemoteMediaDaySummaries(id, date, location, incomplete, numberOfItems)

    @JsonClass(generateAdapter = true)
    data class Complete(
        @field:Json(name = "id")
        override val id: String,
        @field:Json(name = "date")
        override val date: String?,
        @field:Json(name = "location")
        override val location: String,
        @field:Json(name = "incomplete")
        override val incomplete: Boolean,
        @field:Json(name = "numberOfItems")
        override val numberOfItems: Int,
        @field:Json(name = "rating")
        val rating: Int?,
        @field:Json(name = "items")
        val items: List<RemoteMediaItemSummary>,
    ) : RemoteMediaDaySummaries(id, date, location, incomplete, numberOfItems)
}

fun RemoteMediaDaySummaries.Incomplete.toDbModel() = DbRemoteMediaCollections(
    id,
    date,
    location,
    rating = null,
    incomplete,
    numberOfItems,
)