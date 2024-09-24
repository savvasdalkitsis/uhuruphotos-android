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
package com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.service.http

import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.http.response.PersonResponseData
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.service.http.response.PeopleResponseData
import retrofit2.http.GET
import retrofit2.http.Path
import se.ansman.dagger.auto.retrofit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface PeopleService {

    @GET("/api/persons/")
    suspend fun getPeople(): PeopleResponseData

    @GET("/api/persons/{id}/")
    suspend fun getPerson(@Path("id") id: Int): PersonResponseData

}
