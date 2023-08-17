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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CelRowSlot.CelSlot
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CelRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.CelSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState

@Composable
internal fun CelRow(
    modifier: Modifier = Modifier,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    showSyncState: Boolean = false,
    onCelSelected: CelSelected,
    onCelLongPressed: (CelState) -> Unit,
    vararg slots: CelRowSlot
) {
    Row(modifier = modifier) {
        for (item in slots) {
            when (item) {
                is CelSlot -> {
                    val aspectRatio = when {
                        maintainAspectRatio -> item.cel.mediaItem.ratio
                        else -> 1f
                    }
                    Cel(
                        modifier = Modifier
                            .weight(aspectRatio),
                        state = item.cel,
                        onSelected = onCelSelected,
                        aspectRatio = aspectRatio,
                        contentScale = when {
                            maintainAspectRatio -> ContentScale.FillBounds
                            else -> ContentScale.Crop
                        },
                        miniIcons = miniIcons,
                        showSyncState = showSyncState,
                        onLongClick = onCelLongPressed,
                    )
                }
                EmptySlot -> Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}