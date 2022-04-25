package com.savvasdalkitsis.uhuruphotos.feedpage.module

import com.savvasdalkitsis.uhuruphotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule.*
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