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
package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.map.Locations
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapView
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchAction.LoadHeatMap

@Composable
fun SearchLocations(
    action: (SearchAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                end = 12.dp,
                bottom = 12.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Locations"
        )
        Box(
            modifier = Modifier
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { action(LoadHeatMap) },
        ) {
            MapView(
                modifier = Modifier.fillMaxSize(),
                location = Locations.TRAFALGAR_SQUARE,
                zoom = 3f,
                onMapClick = { action(LoadHeatMap) },
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable { action(LoadHeatMap) }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "Photo map",
                style = MaterialTheme.typography.h4,
                color = Color.White,
            )
        }
    }
}