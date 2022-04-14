package com.savvasdalkitsis.librephotos.auth.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CallErrorResponse(
    val detail: String,
    val code: String,
    val messages: List<CallErrorMessage>,
)

@JsonClass(generateAdapter = true)
data class CallErrorMessage(
    @Json(name = "token_class") val tokenClass: String,
    @Json(name = "token_type") val tokenType: String,
    val message: String,
)