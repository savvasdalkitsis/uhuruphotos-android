/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
