/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.map.api.module

import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.CompositeMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.CompositeMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui.GoogleMapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui.GoogleMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui.GoogleMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui.MapBoxMapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui.MapBoxMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui.MapBoxMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui.MapLibreMapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui.MapLibreMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui.MapLibreMapViewStateFactory

object MapModule {

    val mapViewFactoryProvider: MapViewFactoryProvider
        get() = CompositeMapViewFactoryProvider(
            setOf(
                GoogleMapViewFactoryProvider(GoogleMapViewFactory()),
                MapBoxMapViewFactoryProvider(MapBoxMapViewFactory()),
                MapLibreMapViewFactoryProvider(MapLibreMapViewFactory()),
            )
        )

    val mapViewStateFactory: MapViewStateFactory
        get() = CompositeMapViewStateFactory(
            setOf(
                GoogleMapViewStateFactory(),
                MapBoxMapViewStateFactory(),
                MapLibreMapViewStateFactory(),
            )
        )

    val mapBoxInitializer: ApplicationCreated by singleInstance {
        com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer.MapsInitializer()
    }

    val maplibreInitializer: ApplicationCreated by singleInstance {
        com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.initializer.MapsInitializer()
    }

}