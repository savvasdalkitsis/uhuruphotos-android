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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.http.request

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.Credentials
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.zacsweers.redacted.annotations.Redacted

@JsonClass(generateAdapter = true)
data class AuthenticationRequestData(
    @field:Json(name = "username")
    val username: String,
    @Redacted
    @field:Json(name = "password")
    val password: String,
)

val Credentials.toAuthenticationRequestData get() = AuthenticationRequestData(username, password)