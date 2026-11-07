/*
Copyright 2024 Savvas Dalkitsis

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

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteDayPhotosResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteDaysResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteFeedDayResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteFeedResponseData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface RemoteMediaCollectionsService {

    @GET("/api/days/")
    suspend fun getDays(
        @QueryMap filter: Map<String, String>,
    ): RemoteDaysResponseData

    @GET("/api/days/{id}/photos/")
    suspend fun getDayPhotos(
        @Path("id") id: String,
        @Query("after") after: String?,
        @Query("limit") limit: Int,
        @QueryMap filter: Map<String, String>,
    ): RemoteDayPhotosResponseData

    @GET("/api/albums/date/list/")
    suspend fun getLegacyDays(
        @QueryMap filter: Map<String, String>,
    ): RemoteFeedResponseData

    @GET("/api/albums/date/{id}/")
    suspend fun getLegacyDay(
        @Path("id") id: String,
        @Query("page") page: Int,
        @QueryMap filter: Map<String, String>,
    ): RemoteFeedDayResponseData
}
