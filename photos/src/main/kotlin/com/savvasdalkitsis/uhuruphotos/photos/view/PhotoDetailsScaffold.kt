package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.PhotoSheetStyle.BOTTOM
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.ZoomableState

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
        title = { },
        bottomBarContent = {
            val style = LocalPhotoSheetStyle.current
            AnimatedVisibility(visible = style == BOTTOM || state.infoSheetHidden) {
                PhotoDetailsBottomActionBar(state, action)
            }
        },
        actionBarContent = {
            PhotoDetailsActionBar(state, action)
        },
        toolbarColor = { Color.Transparent },
        bottomBarColor = { Color.Transparent },
        topBarDisplayed = state.showUI,
        bottomBarDisplayed = state.showUI,
        navigationIcon = {
            BackNavButton { action(PhotoAction.NavigateBack) }
        },
    ) { contentPadding ->
        when {
            state.isLoading && state.lowResUrl.isEmpty() -> FullProgressBar()
            else -> PhotoDetails(zoomableState, action, state, contentPadding)
        }
    }
}