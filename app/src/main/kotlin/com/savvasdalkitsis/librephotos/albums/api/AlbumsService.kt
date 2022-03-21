package com.savvasdalkitsis.librephotos.albums.api

import com.savvasdalkitsis.librephotos.albums.api.model.Album
import com.savvasdalkitsis.librephotos.albums.api.model.AlbumById
import com.savvasdalkitsis.librephotos.albums.api.model.AlbumsByDate
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumsService {

    @GET("/api/albums/date/list/")
    suspend fun getAlbumsByDate(): AlbumsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getAlbum(@Path("id") id: String): AlbumById
}