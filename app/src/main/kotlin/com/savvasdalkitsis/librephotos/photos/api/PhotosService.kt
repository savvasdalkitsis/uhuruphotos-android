package com.savvasdalkitsis.librephotos.photos.api

import com.savvasdalkitsis.librephotos.photos.api.model.Photos
import retrofit2.http.GET

interface PhotosService {

    @GET("/api/photos/")
    suspend fun getPhotos(): Photos
}