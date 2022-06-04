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
package com.savvasdalkitsis.uhuruphotos.api.albums.service

import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AlbumById
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AlbumsByDate
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.albums.service.model.UserAlbums
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbumsService {

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsByDate(): AlbumsByDate

    @GET("api/albums/auto/list/")
    suspend fun getAutoAlbums(): AutoAlbums

    @GET("api/albums/user/list/")
    suspend fun getUserAlbums(): UserAlbums

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbum(@Path("id") id: String): AlbumById

    @GET("api/albums/auto/{id}/")
    suspend fun getAutoAlbum(@Path("id") id: String): AutoAlbum

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsForPerson(@Query("person") personId: Int): AlbumsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbumForPerson(@Path("id") id: String, @Query("person") personId: Int): AlbumById

    @GET("/api/albums/user/{id}/")
    suspend fun getUserAlbum(@Path("id") id: String): UserAlbum
}