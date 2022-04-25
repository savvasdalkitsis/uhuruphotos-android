package com.savvasdalkitsis.uhuruphotos.search.module

import com.savvasdalkitsis.uhuruphotos.db.Database
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule.HomeNavigationTargetSearch
import com.savvasdalkitsis.uhuruphotos.search.service.SearchService
import com.savvasdalkitsis.uhuruphotos.search.navigation.SearchNavigationTarget
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