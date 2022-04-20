package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.savvasdalkitsis.librephotos.icons.R
import com.savvasdalkitsis.librephotos.infrastructure.extensions.round
import com.savvasdalkitsis.librephotos.map.view.MapView
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.ClickedOnGps
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.HideInfo
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.insets.systemPadding
import com.savvasdalkitsis.librephotos.ui.view.TextWithIcon
import com.savvasdalkitsis.librephotos.ui.view.zoom.ZoomableState

@Composable
fun PhotoDetailsSheet(
    bottomSheetSize: BottomSheetSize,
    state: PhotoState,
    infoSheetState: ModalBottomSheetState,
    zoomableState: ZoomableState,
    action: (PhotoAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .heightIn(min = bottomSheetSize.size.height - systemPadding(WindowInsetsSides.Top).calculateTopPadding())
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
    LaunchedEffect(state.infoSheetState) {
        when (state.infoSheetState) {
            Hidden -> {
                zoomableState.reset()
                infoSheetState.hide()
            }
            Expanded, HalfExpanded -> with(density) {
                if (state.showInfoButton) {
                    zoomableState.animateScaleTo(0.7f)
                    zoomableState.animateOffsetTo(0f, -bottomSheetSize.size.height.toPx() / 4f)
                    infoSheetState.show()
                } else {
                    zoomableState.reset()
                    action(HideInfo)
                }
            }
        }
    }
    if (infoSheetState.currentValue != Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                action(HideInfo)
            }
        }
    }
}

class BottomSheetSize private constructor(
    internal var size: DpSize
) {

    companion object {
        @Composable
        fun rememberBottomSheetSize(): BottomSheetSize {
            val size by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
            return BottomSheetSize(size)
        }
    }
}

fun Modifier.adjustingBottomSheetSize(bottomSheetSize: BottomSheetSize) = composed {
    val density = LocalDensity.current
    onGloballyPositioned { coordinates ->
        with(density) {
            bottomSheetSize.size = coordinates.size.toSize().toDpSize()
        }
    }
}