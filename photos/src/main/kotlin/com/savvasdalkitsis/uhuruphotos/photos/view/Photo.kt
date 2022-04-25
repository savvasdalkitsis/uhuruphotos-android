package com.savvasdalkitsis.uhuruphotos.photos.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.PhotoSheetStyle.BOTTOM
import com.savvasdalkitsis.uhuruphotos.photos.view.PhotoSheetStyle.SIDE
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.zoom.rememberZoomableState
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSize
import com.savvasdalkitsis.uhuruphotos.ui.window.WindowSizeClass.EXPANDED

@Composable
fun Photo(
    state: PhotoState,
    action: (PhotoAction) -> Unit,
) {
    val zoomableState = rememberZoomableState()
    PhotoBackPressHandler(state, action)
    val style = when (WindowSize.LOCAL_WIDTH.current) {
        EXPANDED -> SIDE
        else -> BOTTOM
    }
    CompositionLocalProvider(LocalPhotoSheetStyle provides style) {
        when (style) {
            SIDE -> SideSheetPhotoDetails(state, zoomableState, action)
            BOTTOM -> BottomSheetPhotoDetails(state, zoomableState, action)
        }
    }

}