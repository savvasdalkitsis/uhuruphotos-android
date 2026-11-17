/*
Copyright 2026 Savvas Dalkitsis

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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.R.raw.map_style_dark

@Composable
internal fun rememberGoogleMapProperties(enableMyLocation: Boolean, isDark: Boolean): MapProperties {
    val context = LocalContext.current
    return remember(context, enableMyLocation, isDark) {
        googleMapProperties(context, enableMyLocation, isDark)
    }
}

internal fun googleMapProperties(context: Context, enableMyLocation: Boolean, isDark: Boolean) = MapProperties(
    isMyLocationEnabled = enableMyLocation,
    mapStyleOptions = if (isDark) MapStyleOptions.loadRawResourceStyle(context, map_style_dark) else null,
)
