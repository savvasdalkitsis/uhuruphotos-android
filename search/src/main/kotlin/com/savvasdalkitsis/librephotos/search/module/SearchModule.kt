package com.savvasdalkitsis.librephotos.search.module

import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.db.search.SearchQueries
import com.savvasdalkitsis.librephotos.home.module.HomeModule.HomeNavigationTargetSearch
import com.savvasdalkitsis.librephotos.search.service.SearchService
import com.savvasdalkitsis.librephotos.search.navigation.SearchNavigationTarget
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SearchModule {

    @Provides
    @HomeNavigationTargetSearch
    fun homeNavigationTargetSearch(): String = SearchNavigationTarget.name

    @Provides
    fun searchService(retrofit: Retrofit): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    fun searchQueries(database: Database): SearchQueries = database.searchQueries
}