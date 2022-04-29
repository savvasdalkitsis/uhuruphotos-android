package com.savvasdalkitsis.uhuruphotos.search.service

import com.savvasdalkitsis.uhuruphotos.search.service.model.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.service.model.SearchSuggestions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/api/photos/searchlist/")
    suspend fun search(@Query("search") query: String): SearchResults

    @GET("/api/searchtermexamples/")
    suspend fun getSearchSuggestions(): SearchSuggestions
}