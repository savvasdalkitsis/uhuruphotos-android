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
package com.savvasdalkitsis.uhuruphotos.implementation.library.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.home.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.Refresh
import com.savvasdalkitsis.uhuruphotos.implementation.library.ui.state.LibraryState

@Composable
internal fun Library(
    state: LibraryState,
    homeFeedDisplay: GalleryDisplay,
    isShowingPopUp: Boolean,
    action: (LibraryAction) -> Unit,
    navHostController: NavHostController,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Logo()
                Text(stringResource(string.library))
            }
        },
        actionBarContent = {
           actionBarContent()
        },
        showLibrary = true,
        navController = navHostController,
        homeFeedDisplay = homeFeedDisplay,
    ) { contentPadding ->
        when {
            state.loading
                    && state.autoAlbums == null
                    && state.userAlbums == null -> FullProgressBar()
            else -> SwipeRefresh(
                indicatorPadding = contentPadding,
                state = rememberSwipeRefreshState(isRefreshing = state.loading),
                onRefresh = { action(Refresh) }
            ) {
                LibraryGrid(contentPadding, state, action)
            }
        }
        additionalContent()
    }
}