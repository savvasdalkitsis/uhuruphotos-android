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
package com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonResponseData(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "face_count") val faceCount: Int,
    @field:Json(name = "face_url") val faceUrl: String,
    @field:Json(name = "face_photo_url") val facePhotoUrl: String,
)

fun PersonResponseData.toDbModel() = People(
    id = id,
    name = name,
    faceCount = faceCount,
    faceUrl = faceUrl,
    facePhotoUrl = facePhotoUrl
)
