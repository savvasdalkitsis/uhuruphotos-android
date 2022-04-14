package com.savvasdalkitsis.librephotos.feedpage.module

import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.home.module.HomeModule.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class FeedPageModule {

    @Provides
    @HomeNavigationTargetFeed
    fun homeNavigationTargetFeed(): String = FeedPageNavigationTarget.name

}