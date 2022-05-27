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
package com.savvasdalkitsis.uhuruphotos.autoalbum.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.autoalbum.view.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.ui.view.CommonScaffold

@Composable
fun AutoAlbum(
    state: AutoAlbumState,
    action: (AutoAlbumAction) -> Unit
) {
    CommonScaffold(
        title = { Text(state.title) },
        expandableTopBar = true,
        navigationIcon = {
            BackNavButton {
                action(NavigateBack)
            }
        },
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.feedState.isLoading),
            onRefresh = { action(SwipeToRefresh) }
        ) {
            Feed(
                contentPadding = contentPadding,
                state = state.feedState,
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale,))
                },
            )
        }
    }
}