package com.savvasdalkitsis.librephotos.feedpage.module

import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.home.module.Module.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @HomeNavigationTargetFeed
    fun homeNavigationTargetFeed(): String = FeedPageNavigationTarget.name

}