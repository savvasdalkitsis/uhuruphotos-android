package com.savvasdalkitsis.librephotos.photos.api

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoFavouriteRequest
import com.savvasdalkitsis.librephotos.photos.api.model.PhotoOperationResponse
import com.savvasdalkitsis.librephotos.photos.api.model.PhotoResult
import retrofit2.Response
import retrofit2.http.*

interface PhotosService {

    @GET("/api/photos/{id}/")
    suspend fun getPhoto(@Path("id") id: String): PhotoResult

    @POST("/api/photosedit/favorite/")
    suspend fun setPhotoFavourite(@Body favouriteRequest: PhotoFavouriteRequest): PhotoOperationResponse

    @DELETE("/api/photos/{id}/")
    suspend fun deletePhoto(@Path("id") id: String): Response<Unit>
}