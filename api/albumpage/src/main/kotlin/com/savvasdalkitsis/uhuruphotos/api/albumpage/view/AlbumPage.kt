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
package com.savvasdalkitsis.uhuruphotos.api.albumpage.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.api.people.view.PeopleBar
import com.savvasdalkitsis.uhuruphotos.api.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.api.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction.ChangeFeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.FeedDisplayActionButton

@Composable
fun AlbumPage(
    state: AlbumPageState,
    action: (AlbumPageAction) -> Unit
) {
    CommonScaffold(
        title = { Text(state.title.toText()) },
        expandableTopBar = true,
        navigationIcon = {
            BackNavButton {
                action(NavigateBack)
            }
        },
        actionBarContent = {
            AnimatedVisibility(state.feedState.feedDisplay.iconResource != 0
                    && state.feedState.albums.isNotEmpty()) {
                FeedDisplayActionButton(
                    onChange = { action(ChangeFeedDisplay(it)) },
                    currentFeedDisplay = state.feedState.feedDisplay
                )
            }
        }
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.feedState.isLoading),
            onRefresh = { action(SwipeToRefresh) }
        ) {
            Feed(
                contentPadding = contentPadding,
                state = state.feedState,
                onChangeDisplay = { action(ChangeFeedDisplay(it)) },
                feedHeader = state.people.takeIf { it.isNotEmpty() }?.let {
                    {
                        PeopleBar(
                            modifier = Modifier.animateItemPlacement(),
                            people = state.people,
                            onPersonSelected = { action(PersonSelected(it)) }
                        )
                    }
                },
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale,))
                },
            )
        }
    }
}