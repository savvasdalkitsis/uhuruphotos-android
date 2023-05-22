package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.module

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui.GoogleMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui.GoogleMapViewStateFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@InstallIn(SingletonComponent::class)
@Module
internal abstract class GoogleMapsBindingsModule {

    @Binds
    @IntoSet
    abstract fun mapViewFactoryProvider(mapViewFactoryProvider: GoogleMapViewFactoryProvider):
            MapViewFactoryProvider
    @Binds
    @IntoSet
    abstract fun mapViewStateFactory(mapViewStateFactory: GoogleMapViewStateFactory):
            MapViewStateFactory
}