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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteFeedDayResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteFeedResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryContainerResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteMediaExistsResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteMediaHashOperationResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.request.RemoteMediaItemDeleteRequestData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.request.RemoteMediaItemDeletedRequestData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.request.RemoteMediaItemFavouriteRequestData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteMediaOperationResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface RemoteMediaService {

    @GET("/api/photos/{id}/")
    suspend fun getMediaItem(@Path("id") id: String): RemoteMediaItemResponseData

    @GET("/api/photos/{id}/summary/")
    suspend fun getMediaItemSummary(@Path("id") id: String): RemoteMediaItemSummaryContainerResponseData

    @POST("/api/photosedit/favorite/")
    suspend fun setMediaItemFavourite(
        @Body favouriteRequest: RemoteMediaItemFavouriteRequestData
    ): RemoteMediaOperationResponseData

    @POST("/api/photosedit/setdeleted/")
    suspend fun setMediaItemDeleted(
        @Body setDeletedRequest: RemoteMediaItemDeletedRequestData
    ): Response<RemoteMediaOperationResponseData>

    @HTTP(method = "DELETE", path = "/api/photosedit/delete/", hasBody = true)
    suspend fun deleteMediaItemPermanently(
        @Body deleteRequest: RemoteMediaItemDeleteRequestData
    ): Response<RemoteMediaHashOperationResponseData>

    @GET("/api/albums/date/list/?favorite=true")
    suspend fun getFavouriteMedia(): RemoteFeedResponseData

    @GET("/api/albums/date/{id}/?favorite=true")
    suspend fun getFavouriteMediaCollection(@Path("id") id: String): RemoteFeedDayResponseData

    @GET("/api/albums/date/list/?hidden=true")
    suspend fun getHiddenMedia(): RemoteFeedResponseData

    @GET("/api/albums/date/{id}/?hidden=true")
    suspend fun getHiddenMediaCollection(@Path("id") id: String): RemoteFeedDayResponseData

    @GET("/api/exists/{hash}/")
    suspend fun exists(@Path("hash") hash: String): RemoteMediaExistsResponseData
}