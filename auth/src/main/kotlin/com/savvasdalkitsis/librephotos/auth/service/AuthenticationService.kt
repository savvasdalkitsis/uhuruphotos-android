package com.savvasdalkitsis.librephotos.auth.service

import com.savvasdalkitsis.librephotos.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.librephotos.auth.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.librephotos.auth.service.model.AuthenticationRefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {

    @POST("/api/auth/token/obtain/")
    suspend fun login(@Body credentials: AuthenticationCredentials): AuthenticationObtainResponse

    @POST("/api/auth/token/refresh/")
    suspend fun refreshToken(@Body refreshToken: AuthenticationObtainResponse): AuthenticationRefreshResponse

}
