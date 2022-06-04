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
package com.savvasdalkitsis.uhuruphotos.implementation.library.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.savvasdalkitsis.uhuruphotos.api.compose.copy
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.ui.view.FullProgressBar
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.ChangeAutoAlbumsSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.ChangeUserAlbumsSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.RefreshAutoAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.RefreshUserAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState

@Composable
fun Library(
    state: LibraryState,
    homeFeedDisplay: FeedDisplay,
    action: (LibraryAction) -> Unit,
    navHostController: NavHostController,
) {
    HomeScaffold(
        modifier = Modifier,
        showLibrary = true,
        navController = navHostController,
        userInformationState = null,
        homeFeedDisplay = homeFeedDisplay,
    ) { contentPadding ->
        when {
            state.autoAlbumsLoading
                    && state.userAlbumsLoading
                    && state.autoAlbums.isEmpty()
                    && state.userAlbums.isEmpty() -> FullProgressBar()
            else -> {
                val pages = 2
                val pagerState = rememberPagerState()

                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Spacer(Modifier.height(contentPadding.calculateTopPadding()))
                    LibraryTabs(pagerState, pages)
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        count = pages,
                        state = pagerState,
                        key = { it.choose(auto = "auto", user = "user") },
                        userScrollEnabled = true,
                    ) { page ->
                        with(page) {
                            LibraryPage(
                                contentPadding = contentPadding.copy(top = 0.dp),
                                action = action,
                                isRefreshing = choose(
                                    auto = state.autoAlbumsLoading,
                                    user = state.userAlbumsLoading,
                                ),
                                isEmpty = choose(
                                    auto = state.autoAlbums.isEmpty(),
                                    user = state.userAlbums.isEmpty(),
                                ),
                                refreshAction = choose(
                                    auto = RefreshAutoAlbums,
                                    user = RefreshUserAlbums,
                                ),
                                emptyContentMessage = choose(
                                    auto = R.string.no_auto_albums,
                                    user = R.string.no_user_albums,
                                ),
                                headerTitle = choose(
                                    auto = R.string.auto_generated_albums,
                                    user = R.string.user_created_albums,
                                ),
                                sorting = choose(
                                    auto = state.autoAlbumSorting,
                                    user = state.userAlbumSorting,
                                ),
                                changeSorting = choose(
                                    auto = { ChangeAutoAlbumsSorting(it) },
                                    user = { ChangeUserAlbumsSorting(it) },
                                ),
                                content = choose(
                                    auto = {
                                        state.autoAlbums.forEach { album ->
                                            item(album.id) {
                                                AutoAlbumItem(album, action)
                                            }
                                        }
                                    },
                                    user = {
                                        state.userAlbums.forEach { album ->
                                            item(album.id) {
                                                UserAlbumItem(album, action)
                                            }
                                        }
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun <T> Int.choose(
    auto: T,
    user: T,
): T = if (this == 0) user else auto
