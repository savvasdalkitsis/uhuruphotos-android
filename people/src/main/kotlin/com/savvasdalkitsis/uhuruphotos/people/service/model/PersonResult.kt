package com.savvasdalkitsis.uhuruphotos.people.service.model

import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonResult(
    val name: String,
    val id: Int,
    @field:Json(name = "face_count") val faceCount: Int,
    @field:Json(name = "face_url") val faceUrl: String,
    @field:Json(name = "face_photo_url") val facePhotoUrl: String,
)

fun PersonResult.toPerson() = People(
    id = id,
    name = name,
    faceCount = faceCount,
    faceUrl = faceUrl,
    facePhotoUrl = facePhotoUrl
)