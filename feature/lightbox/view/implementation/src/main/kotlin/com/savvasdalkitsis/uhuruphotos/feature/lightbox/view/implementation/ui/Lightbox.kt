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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ChangedToPage
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import kotlinx.coroutines.flow.collectLatest
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.rememberZoomableState

@Composable
internal fun Lightbox(
    state: LightboxState,
    action: (LightboxAction) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.media.size },
    )

    LaunchedEffect(state.currentIndex) {
        pagerState.scrollToPage(state.currentIndex)
    }
    HorizontalPager(
        state = pagerState,
        pageSpacing = 12.dp,
        key = { page -> state.media.getOrNull(page)?.id?.value ?: page.toString() },
        userScrollEnabled = true,
    ) { index ->
        val zoomableState = rememberZoomableState(
            zoomSpec = ZoomSpec(maxZoomFactor = 3f)
        )
        val scrollState = rememberScrollState()
        LightboxScaffold(state, index, action, zoomableState, scrollState)
        if (pagerState.settledPage != index) {
            LaunchedEffect(Unit) {
                zoomableState.resetZoom(withAnimation = false)
                scrollState.scrollTo(0)
            }
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            action(ChangedToPage(page))
        }
    }
}