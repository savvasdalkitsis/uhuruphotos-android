package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.infrastructure.extensions.round
import com.savvasdalkitsis.librephotos.map.view.MapView
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.ClickedOnGps
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.HideInfo
import com.savvasdalkitsis.librephotos.photos.view.PhotoSheetStyle.BOTTOM
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.TextWithIcon
import com.savvasdalkitsis.librephotos.ui.view.zoom.ZoomableState

@Composable
fun PhotoDetailsSheet(
    modifier: Modifier = Modifier,
    sheetSize: SheetSize,
    state: PhotoState,
    sheetState: SheetState,
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            PhotoDetailsBottomActionBar(state, action)
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
            state.gps?.let { gps ->
                TextWithIcon(
                    modifier = Modifier.clickable { action(ClickedOnGps(gps)) },
                    icon = R.drawable.ic_location_pin,
                    text = "${gps.latitude.round(2)}:${gps.longitude.round(2)}",
                )
            }
        }
    }

    val density = LocalDensity.current
    val sheetStyle = LocalPhotoSheetStyle.current
    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(state.infoSheetHidden, sheetStyle, layoutDirection) {
        when  {
            state.infoSheetHidden -> {
                zoomableState.reset()
                sheetState.hide()
            }
            else -> with(density) {
                if (state.showInfoButton) {
                    zoomableState.animateScaleTo(0.7f)
                    if (sheetStyle == BOTTOM) {
                        zoomableState.animateOffsetTo(0f, -sheetSize.size.height.toPx() / 4f)
                    } else {
                        val multiplier = when (layoutDirection) {
                            Rtl -> 1
                            Ltr -> -1
                        }
                        zoomableState.animateOffsetTo(multiplier * sheetSize.size.width.toPx() / 4f, 0f)
                    }
                    sheetState.show()
                } else {
                    zoomableState.reset()
                    action(HideInfo)
                }
            }
        }
    }
    if (!sheetState.isHidden) {
        DisposableEffect(Unit) {
            onDispose {
                action(HideInfo)
            }
        }
    }
}