package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.librephotos.photos.view.PhotoSheetStyle.BOTTOM
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.ui.view.BackNavButton
import com.savvasdalkitsis.librephotos.ui.view.CommonScaffold
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar
import com.savvasdalkitsis.librephotos.ui.view.zoom.ZoomableState

@Composable
internal fun PhotoDetailsScaffold(
    sheetSize: SheetSize,
    state: PhotoState,
    action: (PhotoAction) -> Unit,
    zoomableState: ZoomableState
) {
    CommonScaffold(
        modifier = Modifier
            .adjustingSheetSize(sheetSize),
        title = {},
        toolbarColor = Color.Transparent,
        bottomBarColor = Color.Transparent,
        topBarDisplayed = state.showUI,
        bottomBarDisplayed = state.showUI,
        navigationIcon = {
            BackNavButton { action(PhotoAction.NavigateBack) }
        },
        actionBarContent = {
            PhotoDetailsActionBar(state, action)
        },
        bottomBarContent = {
            val style = LocalPhotoSheetStyle.current
            AnimatedVisibility(visible = style == BOTTOM || state.infoSheetHidden) {
                PhotoDetailsBottomActionBar(state, action)
            }
        },
    ) { contentPadding ->
        when {
            state.isLoading && state.lowResUrl.isEmpty() -> FullProgressBar()
            else -> PhotoDetails(zoomableState, action, state, contentPadding)
        }
    }
}