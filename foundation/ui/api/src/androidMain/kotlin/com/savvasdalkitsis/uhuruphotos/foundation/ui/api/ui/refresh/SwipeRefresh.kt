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

package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter

@Composable
fun SwipeRefresh(
    indicatorPadding: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val refreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)
    Box(modifier = Modifier
        .recomposeHighlighter()
        .fillMaxSize()
        .pullRefresh(refreshState)
    ) {
        content()
        PullRefreshIndicator(
            modifier = Modifier
                .recomposeHighlighter()
                .align(Alignment.TopCenter)
                .padding(indicatorPadding),
            refreshing = isRefreshing,
            state = refreshState,
        )
    }
}