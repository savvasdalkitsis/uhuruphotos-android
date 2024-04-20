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
package com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import javax.inject.Inject

class GoogleMapViewFactory @Inject constructor(
): MapViewFactory {
    @Composable
    override fun CreateMapView(
        modifier: Modifier,
        mapViewState: MapViewState,
        mapOptions: MapOptions.() -> MapOptions,
        contentPadding: PaddingValues,
        onMapClick: () -> Unit,
        content: @Composable() (MapViewScope.() -> Unit)?
    ) {
        GoogleMapView(
            modifier,
            mapViewState as GoogleMapViewState,
            mapOptions,
            contentPadding,
            onMapClick,
            content,
        )
    }


}
