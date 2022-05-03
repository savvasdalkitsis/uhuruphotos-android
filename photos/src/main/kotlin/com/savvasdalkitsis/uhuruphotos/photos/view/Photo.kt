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