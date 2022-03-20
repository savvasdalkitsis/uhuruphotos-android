package com.savvasdalkitsis.librephotos.auth.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationObtainResponse(
    val refresh: String,
)
