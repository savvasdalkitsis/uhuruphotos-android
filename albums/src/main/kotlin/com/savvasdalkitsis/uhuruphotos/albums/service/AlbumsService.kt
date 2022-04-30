package com.savvasdalkitsis.uhuruphotos.albums.service

import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumById
import com.savvasdalkitsis.uhuruphotos.albums.service.model.AlbumsByDate
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlbumsService {

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsByDate(): AlbumsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbum(@Path("id") id: String): AlbumById

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsForPerson(@Query("person") personId: Int): AlbumsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbumForPerson(@Path("id") id: String, @Query("person") personId: Int): AlbumById
}