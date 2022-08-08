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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.ui.insets.insetsBottom
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction.HideInfo
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoDateTime
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoGps
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoLocation
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoMap
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoMetadata
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.info.MediaItemInfoPeople
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.MediaItemPageState

@Composable
fun MediaItemInfoSheet(
    modifier: Modifier = Modifier,
    state: MediaItemPageState,
    index: Int,
    sheetState: ModalBottomSheetState,
    action: (com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction) -> Unit,
) {
    val mediaItem = state.media[index]
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .padding(16.dp)
            ) {
                MediaItemPageBottomActionBar(state, index, action)
                MediaItemInfoDateTime(mediaItem)
                MediaItemInfoPeople(mediaItem, action)
                MediaItemInfoMap(mediaItem, action)
                MediaItemInfoLocation(mediaItem)
                MediaItemInfoGps(mediaItem, action)
                MediaItemInfoMetadata(mediaItem, action)

                Spacer(modifier = Modifier.height(insetsBottom()))
            }
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    LaunchedEffect(state.infoSheetHidden, layoutDirection) {
        when  {
            state.infoSheetHidden -> {
                sheetState.hide()
            }
            else -> if (state.showInfoButton) {
                sheetState.show()
            } else {
                action(HideInfo)
            }
        }
    }
    if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                action(HideInfo)
            }
        }
    }
}