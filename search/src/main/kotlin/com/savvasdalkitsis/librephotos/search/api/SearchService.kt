package com.savvasdalkitsis.librephotos.search.api

import com.savvasdalkitsis.librephotos.search.api.model.SearchResults
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("/api/photos/searchlist/")
    suspend fun search(@Query("search") query: String): SearchResults
}