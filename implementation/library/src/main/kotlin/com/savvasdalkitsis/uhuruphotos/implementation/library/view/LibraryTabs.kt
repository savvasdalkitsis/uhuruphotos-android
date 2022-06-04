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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import kotlinx.coroutines.launch

@Composable
internal fun LibraryTabs(pagerState: PagerState, pages: Int) {
    val scope = rememberCoroutineScope()
    TabRow(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.background,
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        for (page in 0 until pages) {
            with(page) {
                Tab(
                    text = {
                        Text(
                            stringResource(
                                choose(
                                    auto = R.string.auto_albums,
                                    user = R.string.user_albums,
                                )
                            )
                        )
                    },
                    selected = pagerState.currentPage == page,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    },
                )
            }
        }
    }
}