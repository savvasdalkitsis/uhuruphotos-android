package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.module

import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.initializer.MapsInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui.MapLibreMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui.MapLibreMapViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@InstallIn(SingletonComponent::class)
@Module
internal abstract class MapLibreBindingsModule {

    @Binds
    @IntoSet
    abstract fun mapsInitializer(mapsInitializer: MapsInitializer):
            ApplicationCreated

    @Binds
    @IntoSet
    abstract fun mapViewFactoryProvider(mapViewFactoryProvider: MapLibreMapViewFactoryProvider):
            MapViewFactoryProvider
    @Binds
    @IntoSet
    abstract fun mapViewStateFactory(mapViewStateFactory: MapLibreMapViewStateFactory):
            MapViewStateFactory
}