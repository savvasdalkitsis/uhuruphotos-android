package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions

interface MapViewFactory {

    @Composable
    fun CreateMapView(
        modifier: Modifier,
        mapViewState: MapViewState,
        mapOptions: MapOptions.() -> MapOptions,
        contentPadding: PaddingValues,
        onMapClick: () -> Unit,
        content: @Composable (MapViewScope.() -> Unit)?,
    )
}