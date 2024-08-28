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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model.UserAlbumEditModel
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model.UserAlbumServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.model.UserAlbumServiceUpdateModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface UserAlbumService {

    @GET("/api/albums/user/{id}/")
    suspend fun getUserAlbum(@Path("id") id: String): UserAlbumServiceModel

    @PATCH("/api/albums/user/edit/{id}/")
    suspend fun addMediaToUserAlbum(@Path("id") id: String, @Body body: UserAlbumEditModel): UserAlbumServiceUpdateModel

    @POST("/api/albums/user/edit/")
    suspend fun createUserAlbum(@Body body: UserAlbumEditModel): UserAlbumServiceUpdateModel

    @DELETE("/api/albums/user/edit/{id}/")
    suspend fun deleteUserAlbum(@Path("id") id: String)
}