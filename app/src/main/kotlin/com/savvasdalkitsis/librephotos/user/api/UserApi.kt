package com.savvasdalkitsis.librephotos.user.api

import com.savvasdalkitsis.librephotos.user.api.model.UsersResult
import retrofit2.http.GET

interface UserApi {

    @GET("/api/user/")
    suspend fun getUser(): UsersResult
}