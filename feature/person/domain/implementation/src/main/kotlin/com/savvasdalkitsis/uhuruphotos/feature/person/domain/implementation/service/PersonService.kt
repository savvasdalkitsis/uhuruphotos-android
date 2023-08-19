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
package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionById
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface PersonService {

    @GET("/api/albums/date/list/")
    suspend fun getMediaCollectionsForPerson(@Query("person") personId: Int): RemoteMediaCollectionsByDate

    @GET("/api/albums/date/{id}/")
    suspend fun getMediaCollectionForPerson(@Path("id") id: String, @Query("person") personId: Int): RemoteMediaCollectionById
}