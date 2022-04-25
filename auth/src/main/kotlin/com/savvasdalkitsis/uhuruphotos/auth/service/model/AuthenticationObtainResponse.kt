package com.savvasdalkitsis.uhuruphotos.auth.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationObtainResponse(
    val refresh: String,
)
