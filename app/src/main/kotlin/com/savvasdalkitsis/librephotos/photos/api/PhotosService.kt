package com.savvasdalkitsis.librephotos.photos.api

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoFavouriteRequest
import com.savvasdalkitsis.librephotos.photos.api.model.PhotoOperationResponse
import com.savvasdalkitsis.librephotos.photos.api.model.PhotoResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PhotosService {

    @GET("/api/photos/{url}/")
    suspend fun getPhoto(@Path("url") url: String): PhotoResult

    @POST("api/photosedit/favorite/")
    suspend fun setPhotoFavourite(@Body favouriteRequest: PhotoFavouriteRequest): PhotoOperationResponse
}