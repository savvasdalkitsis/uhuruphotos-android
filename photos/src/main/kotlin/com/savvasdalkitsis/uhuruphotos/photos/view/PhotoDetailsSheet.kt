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
package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.round
import com.savvasdalkitsis.uhuruphotos.map.view.MapView
import com.savvasdalkitsis.uhuruphotos.people.api.view.PeopleBar
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction.*
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.TextWithIcon

@Composable
fun PhotoDetailsSheet(
    modifier: Modifier = Modifier,
    state: PhotoState,
    index: Int,
    sheetState: ModalBottomSheetState,
    action: (PhotoAction) -> Unit,
) {
    val photo = state.photos[index]
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                PhotoDetailsBottomActionBar(state, index, action)
                TextWithIcon(
                    icon = R.drawable.ic_calendar,
                    text = photo.dateAndTime,
                )
                photo.gps?.let { gps ->
                    MapView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        location = gps,
                        zoom = 15f,
                        onMapClick = { action(ClickedOnMap(gps)) },
                        mapSettings = {
                            copy(
                                zoomControlsEnabled = true,
                            )
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = gps),
                        )
                    }
                }
                if (photo.location.isNotEmpty()) {
                    TextWithIcon(
                        icon = R.drawable.ic_location_place,
                        text = photo.location,
                    )
                }
                photo.gps?.let { gps ->
                    TextWithIcon(
                        modifier = Modifier.clickable { action(ClickedOnGps(gps)) },
                        icon = R.drawable.ic_location_pin,
                        text = "${gps.latitude.round(2)}:${gps.longitude.round(2)}",
                    )
                }
            }
            if (photo.peopleInPhoto.isNotEmpty()) {
                PeopleBar(
                    people = photo.peopleInPhoto,
                    onPersonSelected = { action(PersonSelected(it)) }
                )
            }
        }
    }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(state.infoSheetHidden, layoutDirection) {
        when  {
            state.infoSheetHidden -> {
                sheetState.hide()
            }
            else -> with(density) {
                if (state.showInfoButton) {
                    sheetState.show()
                } else {
                    action(HideInfo)
                }
            }
        }
    }
    if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                action(HideInfo)
            }
        }
    }
}