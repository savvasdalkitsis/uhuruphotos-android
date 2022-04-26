package com.savvasdalkitsis.uhuruphotos.user.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersResult(
    val results: List<UserResult>
)