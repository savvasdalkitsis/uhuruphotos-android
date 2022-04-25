package com.savvasdalkitsis.uhuruphotos.feedpage.module

import com.savvasdalkitsis.uhuruphotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class FeedBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: FeedPageNavigationTarget): NavigationTarget
}