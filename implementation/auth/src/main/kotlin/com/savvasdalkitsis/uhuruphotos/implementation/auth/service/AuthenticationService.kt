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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.service

import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.model.AuthenticationRefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("/api/auth/token/obtain/")
    suspend fun login(@Body credentials: AuthenticationCredentials): AuthenticationObtainResponse

    @POST("/api/auth/token/refresh/")
    suspend fun refreshToken(@Body refreshToken: AuthenticationObtainResponse): AuthenticationRefreshResponse

}
