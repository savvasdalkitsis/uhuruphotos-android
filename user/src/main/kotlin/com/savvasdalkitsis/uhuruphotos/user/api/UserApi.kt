package com.savvasdalkitsis.uhuruphotos.user.api

import com.savvasdalkitsis.uhuruphotos.user.api.model.UsersResult
import retrofit2.http.GET

interface UserApi {

    @GET("/api/user/")
    suspend fun getUser(): UsersResult
}