/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaDayCompleteResponse(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "location")
    val location: String,
    @field:Json(name = "incomplete")
    val incomplete: Boolean,
    @field:Json(name = "numberOfItems")
    val numberOfItems: Int,
    @field:Json(name = "rating")
    val rating: Int?,
    @field:Json(name = "items")
    val items: List<RemoteMediaItemSummaryResponse>,
)