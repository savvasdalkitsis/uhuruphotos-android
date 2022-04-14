package com.savvasdalkitsis.librephotos.search.module

import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.search.SearchQueries
import com.savvasdalkitsis.librephotos.search.api.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun searchService(retrofit: Retrofit): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    fun searchQueries(database: Database): SearchQueries = database.searchQueries
}