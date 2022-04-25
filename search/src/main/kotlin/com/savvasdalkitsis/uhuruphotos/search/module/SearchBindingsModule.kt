package com.savvasdalkitsis.uhuruphotos.search.module

import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.search.navigation.SearchNavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: SearchNavigationTarget): NavigationTarget
}