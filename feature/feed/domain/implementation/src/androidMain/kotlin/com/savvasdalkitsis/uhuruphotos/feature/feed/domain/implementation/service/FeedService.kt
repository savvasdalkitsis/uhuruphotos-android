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

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionById
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface FeedService {

    @GET("api/albums/date/list/")
    suspend fun getRemoteMediaCollectionsByDate(): RemoteMediaCollectionsByDate

    @GET("api/albums/date/{id}/")
    suspend fun getRemoteMediaCollection(@Path("id") id: String, @Query("page") page: Int): RemoteMediaCollectionById
}