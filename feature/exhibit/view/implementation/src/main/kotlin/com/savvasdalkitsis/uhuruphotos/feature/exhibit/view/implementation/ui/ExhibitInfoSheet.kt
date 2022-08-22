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
package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction.HideInfo
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoDateTime
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoGps
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoLocation
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoMap
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoMetadata
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.info.ExhibitInfoPeople
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.ExhibitState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsBottom

@Composable
fun ExhibitInfoSheet(
    modifier: Modifier = Modifier,
    state: ExhibitState,
    index: Int,
    sheetState: ModalBottomSheetState,
    action: (ExhibitAction) -> Unit,
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
                ExhibitBottomActionBar(state, index, action)
                ExhibitInfoDateTime(mediaItem)
                ExhibitInfoPeople(mediaItem, action)
                ExhibitInfoMap(mediaItem, action)
                ExhibitInfoLocation(mediaItem)
                ExhibitInfoGps(mediaItem, action)
                ExhibitInfoMetadata(mediaItem, action)

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