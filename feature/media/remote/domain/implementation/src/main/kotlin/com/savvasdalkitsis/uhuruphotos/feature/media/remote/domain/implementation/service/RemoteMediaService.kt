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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaHashOperationResponseServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemDeleteRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemDeletedRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaItemFavouriteRequestServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaOperationResponseServiceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.model.RemoteMediaResultsServiceModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteMediaService {

    @GET("/api/photos/{id}/")
    suspend fun getMediaItem(@Path("id") id: String): RemoteMediaItem

    @POST("/api/photosedit/favorite/")
    suspend fun setMediaItemFavourite(
        @Body favouriteRequest: RemoteMediaItemFavouriteRequestServiceModel
    ): RemoteMediaOperationResponseServiceModel

    @POST("/api/photosedit/setdeleted/")
    suspend fun setMediaItemDeleted(
        @Body setDeletedRequest: RemoteMediaItemDeletedRequestServiceModel
    ): Response<RemoteMediaOperationResponseServiceModel>

    @HTTP(method = "DELETE", path = "/api/photosedit/delete/", hasBody = true)
    suspend fun deleteMediaItemPermanently(
        @Body deleteRequest: RemoteMediaItemDeleteRequestServiceModel
    ): Response<RemoteMediaHashOperationResponseServiceModel>

    @GET("/api/photos/favorites/")
    suspend fun getFavouriteMedia(): RemoteMediaResultsServiceModel

    @GET("/api/photos/hidden/")
    suspend fun getHiddenMedia(): RemoteMediaResultsServiceModel
}