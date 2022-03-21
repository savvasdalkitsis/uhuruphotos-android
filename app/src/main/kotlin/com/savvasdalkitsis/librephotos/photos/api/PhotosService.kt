package com.savvasdalkitsis.librephotos.photos.api

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoResult
import com.savvasdalkitsis.librephotos.photos.api.model.Photos
import retrofit2.http.GET
import retrofit2.http.Path

interface PhotosService {

    @GET("/api/photos/")
    suspend fun getPhotos(): Photos

    @GET("/api/photos/{url}/")
    suspend fun getPhoto(@Path("url") url: String): PhotoResult
}