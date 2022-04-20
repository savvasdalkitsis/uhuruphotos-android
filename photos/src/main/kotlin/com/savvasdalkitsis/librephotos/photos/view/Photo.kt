package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction.NavigateBack
import com.savvasdalkitsis.librephotos.photos.view.BottomSheetSize.Companion.rememberBottomSheetSize
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
    val bottomSheetSize = rememberBottomSheetSize()

    PhotoBackPressHandler(state, action)

    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(12.dp),
        sheetContent = {
            PhotoDetailsSheet(bottomSheetSize, state, infoSheetState, zoomableState, action)
        },
        sheetState = infoSheetState
    ) {
        CommonScaffold(
            modifier = Modifier
                .adjustingBottomSheetSize(bottomSheetSize),
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
}