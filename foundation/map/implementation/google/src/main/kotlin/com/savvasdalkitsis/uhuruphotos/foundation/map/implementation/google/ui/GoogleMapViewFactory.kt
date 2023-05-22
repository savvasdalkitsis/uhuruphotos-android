package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui

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
