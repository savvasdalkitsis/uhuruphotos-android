package com.savvasdalkitsis.librephotos.auth.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationCredentials(
    val username: String,
    val password: String,
)
