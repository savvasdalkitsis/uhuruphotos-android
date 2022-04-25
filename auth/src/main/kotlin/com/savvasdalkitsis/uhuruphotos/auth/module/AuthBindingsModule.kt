package com.savvasdalkitsis.uhuruphotos.auth.module

import com.savvasdalkitsis.uhuruphotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindingsModule {

    @Binds
    @IntoSet
    abstract fun webLoginNavigationTarget(navigationTarget: WebLoginNavigationTarget): NavigationTarget
}