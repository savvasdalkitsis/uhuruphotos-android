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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.savvasdalkitsis.uhuruphotos.api.ui.insets.insetsTop
import com.savvasdalkitsis.uhuruphotos.api.ui.view.SheetHandle
import com.savvasdalkitsis.uhuruphotos.api.ui.view.SheetSize
import com.savvasdalkitsis.uhuruphotos.api.ui.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ChangedToPage
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun BottomSheetPhotoDetails(
    state: PhotoState,
    action: (PhotoAction) -> Unit
) {
    val infoSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val sheetSize by SheetSize.rememberSheetSize()
    val pagerState = rememberPagerState()
    LaunchedEffect(state.photos.size, pagerState.pageCount) {
        if (pagerState.pageCount > state.currentIndex && state.currentIndex >= 0) {
            pagerState.scrollToPage(state.currentIndex)
        }
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            action(ChangedToPage(page))
        }
    }

    HorizontalPager(
        count = state.photos.size,
        state = pagerState,
        itemSpacing = 12.dp,
        key = { page -> state.photos.getOrNull(page)?.id ?: page.toString() },
        userScrollEnabled = true,
    ) { index ->
        ModalBottomSheetLayout(
            sheetElevation = 0.dp,
            sheetBackgroundColor = Color.Transparent,
            sheetContent = {
                Spacer(modifier = Modifier.height(insetsTop()))
                Surface(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .clip(RoundedCornerShape(12.dp))
                        .heightIn(min = max(100.dp, sheetSize.size.height - insetsTop()))
                        .let {
                            when (LocalWindowSize.current.widthSizeClass) {
                                WindowWidthSizeClass.Compact -> it
                                else -> it.widthIn(max = 460.dp)
                            }
                        }
                        .background(MaterialTheme.colors.background)
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        SheetHandle()
                        PhotoDetailsSheet(
                            state = state,
                            index = index,
                            sheetState = infoSheetState,
                            action = action
                        )
                    }
                }
            },
            sheetState = infoSheetState
        ) {
            PhotoDetailsScaffold(sheetSize, state, index, action)
        }
    }
}