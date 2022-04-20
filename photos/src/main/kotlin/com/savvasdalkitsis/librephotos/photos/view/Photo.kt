package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.*
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.HideInfo
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.NavigateBack
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.BackNavButton
import com.savvasdalkitsis.librephotos.ui.view.CommonScaffold
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar
import com.savvasdalkitsis.librephotos.ui.view.zoom.rememberZoomableState

@Composable
fun Photo(
    state: PhotoState,
    action: (PhotoAction) -> Unit,
) {
    val infoSheetState = rememberModalBottomSheetState(initialValue = Hidden)
    val zoomableState = rememberZoomableState()
    var size by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
    val density = LocalDensity.current

    PhotoBackPressHandler(state, action)

    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(12.dp),
        sheetContent = {
            PhotoDetailsSheet(size, state, action)
        },
        sheetState = infoSheetState
    ) {
        CommonScaffold(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        size = coordinates.size.toSize().toDpSize()
                    }
                },
            title = {},
            toolbarColor = Color.Transparent,
            bottomBarColor = Color.Transparent,
            topBarDisplayed = state.showUI,
            bottomBarDisplayed = state.showUI,
            navigationIcon = {
                BackNavButton { action(NavigateBack) }
            },
            actionBarContent = {
                PhotoDetailsActionBar(state, action)
            },
            bottomBarContent = {
                PhotoDetailsBottomActionBar(state, action)
            },
        ) { contentPadding ->
            when {
                state.isLoading && state.lowResUrl.isEmpty() -> FullProgressBar()
                else -> PhotoDetails(zoomableState, action, state, contentPadding)
            }
        }
    }
    LaunchedEffect(state.infoSheetState) {
        when (state.infoSheetState) {
            Hidden -> {
                zoomableState.reset()
                infoSheetState.hide()
            }
            Expanded, HalfExpanded -> with (density) {
                if (state.showInfoButton) {
                    zoomableState.animateScaleTo(0.7f)
                    zoomableState.animateOffsetTo(0f, -size.height.toPx() / 4f)
                    infoSheetState.show()
                } else {
                    action(HideInfo)
                    zoomableState.reset()
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