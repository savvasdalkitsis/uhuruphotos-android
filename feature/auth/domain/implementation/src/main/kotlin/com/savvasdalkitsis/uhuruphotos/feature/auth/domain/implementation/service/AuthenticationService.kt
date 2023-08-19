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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationRefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface AuthenticationService {

    @POST("/api/auth/token/obtain/")
    suspend fun login(@Body credentials: AuthenticationCredentials): AuthenticationObtainResponse

    @POST("/api/auth/token/refresh/")
    suspend fun refreshToken(@Body refreshToken: AuthenticationObtainResponse): Response<AuthenticationRefreshResponse>

}
