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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteFeedDayResult
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteFeedResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface FeedService {

    @GET("/api/albums/date/list/")
    suspend fun getRemoteFeed(): RemoteFeedResult

    @GET("/api/albums/date/list/")
    suspend fun getRemoteFeedSince(
        @Query("last_modified") since: String, // example: "2024-08-02T21:09:57Z"
    ): RemoteFeedResult

    @GET("/api/albums/date/{id}/")
    suspend fun getRemoteFeedDay(@Path("id") id: String, @Query("page") page: Int): RemoteFeedDayResult

    @GET("/api/albums/date/{id}/")
    suspend fun getRemoteFeedDaySince(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("last_modified") since: String, // example: "2024-08-02T21:09:57Z"
    ): RemoteFeedDayResult
}