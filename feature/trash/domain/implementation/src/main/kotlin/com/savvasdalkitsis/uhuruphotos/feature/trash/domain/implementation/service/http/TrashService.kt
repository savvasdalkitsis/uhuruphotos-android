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
package com.savvasdalkitsis.uhuruphotos.feature.trash.domain.implementation.service.http

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteFeedDayResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteFeedResponseData
import retrofit2.http.GET
import retrofit2.http.Path
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface TrashService {

    @GET("/api/albums/date/list/?in_trashcan=true")
    suspend fun getTrash(): RemoteFeedResponseData

    @GET("/api/albums/date/{id}/?in_trashcan=true")
    suspend fun getTrashMediaCollection(@Path("id") id: String): RemoteFeedDayResponseData

}