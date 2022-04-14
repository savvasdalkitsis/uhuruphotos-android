package com.savvasdalkitsis.librephotos.user.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersResult(
    val results: List<UserResult>
)