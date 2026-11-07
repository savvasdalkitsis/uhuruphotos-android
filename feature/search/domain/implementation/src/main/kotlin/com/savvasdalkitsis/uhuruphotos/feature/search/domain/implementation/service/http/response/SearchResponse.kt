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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.model.SearchHitModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @field:Json(name = "id")
    val id: String? = null,
    @field:Json(name = "date")
    val date: String? = null,
    @field:Json(name = "location")
    val location: String? = null,
    @field:Json(name = "items")
    val items: List<RemoteMediaItemSummaryResponse>? = null,
)

private const val NO_TIMESTAMP = "No timestamp"

fun SearchResponse.toSearchHits(): List<SearchHitModel> = when (items) {
    null -> listOfNotNull(id?.let { SearchHitModel(it, dayOf(date), location) })
    else -> items.map { SearchHitModel(it.id, date ?: NO_TIMESTAMP, location) }
}

private fun dayOf(date: String?): String = date?.take(DAY_LENGTH)?.ifEmpty { null } ?: NO_TIMESTAMP

private const val DAY_LENGTH = 10
