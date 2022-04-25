package com.savvasdalkitsis.uhuruphotos.auth.service

import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationRefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("/api/auth/token/obtain/")
    suspend fun login(@Body credentials: AuthenticationCredentials): AuthenticationObtainResponse

    @POST("/api/auth/token/refresh/")
    suspend fun refreshToken(@Body refreshToken: AuthenticationObtainResponse): AuthenticationRefreshResponse

}
