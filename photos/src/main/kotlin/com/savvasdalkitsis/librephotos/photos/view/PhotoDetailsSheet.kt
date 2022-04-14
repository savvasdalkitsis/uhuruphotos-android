package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.infrastructure.extensions.round
import com.savvasdalkitsis.librephotos.infrastructure.ui.insets.systemPadding
import com.savvasdalkitsis.librephotos.map.view.MapView
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.TextWithIcon

@Composable
fun PhotoDetailsSheet(
    size: DpSize,
    state: PhotoState,
    action: (PhotoAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .heightIn(min = size.height - systemPadding(WindowInsetsSides.Top).calculateTopPadding())
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.onBackground)
                        .width(24.dp)
                        .height(4.dp)
                )
            }
            TextWithIcon(
                icon = R.drawable.ic_calendar,
                text = state.dateAndTime,
            )
            state.gps?.let { gps ->
                MapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    location = gps,
                    zoom = 15f,
                    onMapClick = { action(PhotoAction.ClickedOnMap(gps)) },
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
            TextWithIcon(
                icon = R.drawable.ic_location_place,
                text = state.location,
            )
            state.gps?.let {
                TextWithIcon(
                    icon = R.drawable.ic_location_pin,
                    text = "${it.latitude.round(2)}:${it.longitude.round(2)}",
                )
            }
        }
    }
}