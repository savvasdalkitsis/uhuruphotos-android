package com.savvasdalkitsis.uhuruphotos.server.module

import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ServerBindingsModule {

    @Binds
    @IntoSet
    abstract fun navigationTarget(navigationTarget: ServerNavigationTarget): NavigationTarget
}