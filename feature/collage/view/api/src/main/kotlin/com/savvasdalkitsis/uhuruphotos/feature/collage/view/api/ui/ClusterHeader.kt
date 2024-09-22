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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.previewClusterStateEmpty
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon

@Composable
internal fun ClusterHeader(
    modifier: Modifier = Modifier,
    state: ClusterState,
    showSelectionHeader: Boolean,
    title: String = state.displayTitle,
    location: String? = state.location,
    onRefreshClicked: () -> Unit = {},
    onSelectionHeaderClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = showSelectionHeader) {
            val icon by remember(state.cels) {
                derivedStateOf {
                    when {
                        state.cels.any { it.selectionMode == UNSELECTED } ->
                            drawable.ic_check_circle
                        else -> drawable.ic_clear
                    }
                }
            }
            ActionIcon(
                onClick = onSelectionHeaderClicked,
                icon = icon,
            )
        }
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 16.dp,
                    bottom = 16.dp,
                )
                .weight(1f),
        ) {
            Text(
                text = title,
                style = remember {
                    TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            )
            val locationDisplay by remember {
                derivedStateOf {
                    location.takeIf { !it.isNullOrEmpty() }
                }
            }
            locationDisplay?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = remember {
                        TextStyle.Default.copy(fontWeight = FontWeight.Light)
                    }
                )
            }
        }
        if (state.showRefreshIcon) {
            ActionIcon(
                iconModifier = Modifier.alpha(0.6f),
                onClick = onRefreshClicked,
                icon = drawable.ic_refresh,
            )
        }
    }
}

@Preview
@Composable
private fun CollageGroupHeaderPreview() {
    PreviewAppTheme {
        ClusterHeader(
            modifier = Modifier,
            state = previewClusterStateEmpty,
            showSelectionHeader = false,
        )
    }
}

@Preview
@Composable
private fun CollageGroupPreviewSelection() {
    PreviewAppTheme {
        ClusterHeader(
            modifier = Modifier,
            state = previewClusterStateEmpty,
            showSelectionHeader = true,
        )
    }
}