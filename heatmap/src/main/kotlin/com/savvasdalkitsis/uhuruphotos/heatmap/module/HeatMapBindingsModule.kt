package com.savvasdalkitsis.uhuruphotos.heatmap.module

import com.savvasdalkitsis.uhuruphotos.heatmap.navigation.HeatMapNavigationTarget
import com.savvasdalkitsis.uhuruphotos.navigation.NavigationTarget
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class HeatMapBindingsModule {

    @Binds
    @IntoSet
    abstract fun heatMapNavigationTarget(target: HeatMapNavigationTarget): NavigationTarget
}