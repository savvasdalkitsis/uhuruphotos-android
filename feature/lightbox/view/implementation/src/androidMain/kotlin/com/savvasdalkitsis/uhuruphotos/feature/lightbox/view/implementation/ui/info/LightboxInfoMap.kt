/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.icerock.moko.resources.compose.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ClickedOnMap
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.rememberMapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader

@Composable
internal fun LightboxInfoMap(
    mediaItem: SingleMediaItemState,
    action: (LightboxAction) -> Unit
) {
    mediaItem.details.latLon?.let { gps ->
        Column(
            verticalArrangement = spacedBy(8.dp)
        ) {
            SectionHeader(title = stringResource(strings.location)) {
                OutlinedButton(
                    onClick = { action(ClickedOnMap(gps)) }
                ) {
                    Text(stringResource(strings.open_in_maps))
                }
            }
            MapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.large),
                mapViewState = rememberMapViewState(
                    initialPosition = gps,
                    initialZoom = 15f,
                ),
                mapOptions = {
                    copy(
                        zoomControlsEnabled = true,
                    )
                },
                contentPadding = PaddingValues(bottom = 24.dp),
                onMapClick = { action(ClickedOnMap(gps)) }
            ) {
                Marker(gps)
            }
        }
    }
}