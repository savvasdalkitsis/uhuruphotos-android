package com.savvasdalkitsis.uhuruphotos.home.module

import com.savvasdalkitsis.uhuruphotos.home.navigation.HomeNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: HomeNavigationTarget): NavigationTarget
}