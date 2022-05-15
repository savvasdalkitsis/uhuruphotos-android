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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoAction
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.ui.view.*
import com.savvasdalkitsis.uhuruphotos.ui.window.LocalWindowSize

@Composable
internal fun PhotoDetailsScaffold(
    sheetSize: SheetSize,
    state: PhotoState,
    index: Int,
    action: (PhotoAction) -> Unit,
) {
    CommonScaffold(
        modifier = Modifier
            .adjustingSheetSize(sheetSize),
        title = { },
        bottomBarContent = {
            AnimatedVisibility(
                visible = LocalWindowSize.current.widthSizeClass == Compact || state.infoSheetHidden,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                PhotoDetailsBottomActionBar(state, index, action)
            }
        },
        actionBarContent = {
            PhotoDetailsActionBar(state, index, action)
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
            state.isLoading && state.photos[index].lowResUrl.isEmpty() -> FullProgressBar()
            else -> PhotoDetails(action, state, index, contentPadding)
        }
    }
}