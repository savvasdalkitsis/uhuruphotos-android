package com.savvasdalkitsis.uhuruphotos.user.service

import com.savvasdalkitsis.uhuruphotos.user.service.model.UsersResult
import retrofit2.http.GET

interface UserService {

    @GET("/api/user/")
    suspend fun getUser(): UsersResult
}