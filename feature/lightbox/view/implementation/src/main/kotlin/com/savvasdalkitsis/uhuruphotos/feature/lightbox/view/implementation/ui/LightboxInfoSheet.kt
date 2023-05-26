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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.HideInfo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoDateTime
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoGps
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoLocation
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoMap
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoMetadata
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info.LightboxInfoPeople
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsBottom

@Composable
fun LightboxInfoSheet(
    modifier: Modifier = Modifier,
    state: LightboxState,
    index: Int,
    sheetState: SheetState,
    action: (LightboxAction) -> Unit,
) {
    val mediaItem = state.media[index]
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
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
                LightboxBottomActionBar(state, index, action)
                LightboxInfoDateTime(mediaItem)
                LightboxInfoPeople(mediaItem, action)
                LightboxInfoMap(mediaItem, action)
                LightboxInfoLocation(mediaItem)
                LightboxInfoGps(mediaItem, action)
                LightboxInfoMetadata(mediaItem, action)

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
    if (sheetState.currentValue != SheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                action(HideInfo)
            }
        }
    }
}