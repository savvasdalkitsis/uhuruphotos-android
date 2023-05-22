package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.module

import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.initializer.MapsInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.ui.MapBoxMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.mapbox.ui.MapBoxMapViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@InstallIn(SingletonComponent::class)
@Module
internal abstract class MapBoxBindingsModule {

    @Binds
    @IntoSet
    abstract fun mapsInitializer(mapsInitializer: MapsInitializer):
            ApplicationCreated

    @Binds
    @IntoSet
    abstract fun mapViewFactoryProvider(mapViewFactoryProvider: MapBoxMapViewFactoryProvider):
            MapViewFactoryProvider
    @Binds
    @IntoSet
    abstract fun mapViewStateFactory(mapViewStateFactory: MapBoxMapViewStateFactory):
            MapViewStateFactory
}