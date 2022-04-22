package com.savvasdalkitsis.librephotos.albums.service

import com.savvasdalkitsis.librephotos.albums.service.model.AlbumById
import com.savvasdalkitsis.librephotos.albums.service.model.AlbumsByDate
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumsService {

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsByDate(): AlbumsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbum(@Path("id") id: String): AlbumById
}