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
import com.squareup.moshi.JsonClass

sealed class RemoteFeedDay(
    open val id: String,
    open val date: String?,
    open val location: String,
    open val incomplete: Boolean,
    open val numberOfItems: Int,
) {

    @JsonClass(generateAdapter = true)
    data class Incomplete(
        override val id: String,
        override val date: String?,
        override val location: String,
        override val incomplete: Boolean,
        override val numberOfItems: Int,
    ) : RemoteFeedDay(id, date, location, incomplete, numberOfItems)

    @JsonClass(generateAdapter = true)
    data class Complete(
        override val id: String,
        override val date: String?,
        override val location: String,
        override val incomplete: Boolean,
        override val numberOfItems: Int,
        val rating: Int?,
        val items: List<RemoteMediaItemSummary>,
    ) : RemoteFeedDay(id, date, location, incomplete, numberOfItems) {
        fun toIncomplete() = Incomplete(
            id, date, location, incomplete, numberOfItems
        )
    }
}

fun RemoteFeedDay.Incomplete.toDbModel() = DbRemoteMediaCollections(
    id,
    date,
    location,
    rating = null,
    incomplete,
    numberOfItems,
)