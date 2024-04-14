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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.model.SearchResults
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.model.SearchSuggestions
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import se.ansman.dagger.auto.ktorfit.AutoProvideService
import javax.inject.Singleton

@AutoProvideService
@Singleton
interface SearchService {

    @GET("api/photos/searchlist/")
    suspend fun search(@Query("search") query: String): SearchResults

    @GET("api/searchtermexamples/")
    suspend fun getSearchSuggestions(): SearchSuggestions
}