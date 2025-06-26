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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelSelectionModeState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors

@Composable
fun SharedTransitionScope.Vitrine(
    modifier: Modifier = Modifier,
    state: VitrineState,
    selectable: Boolean = true,
    onSelected: () -> Unit,
    shape: Shape = RectangleShape,
) {
    Column(
        modifier = modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(shape)
            .let { if(selectable) it.clickable(onClick = onSelected) else it },
    ) {
        Row {
            GridItem(state.cel1, this@Vitrine)
            GridItem(state.cel2, this@Vitrine)
        }
        Row {
            GridItem(state.cel3, this@Vitrine)
            GridItem(state.cel4, this@Vitrine)
        }
    }
}

@Composable
private fun RowScope.GridItem(celState: CelState?, sharedTransitionScope: SharedTransitionScope) {
    if (celState != null) {
        with (sharedTransitionScope) {
            Cel(
                modifier = Modifier
                    .weight(1f),
                state = celState,
                onSelected = { _ -> },
                aspectRatio = 1f,
                contentScale = ContentScale.Crop,
                miniIcons = true,
                selectionMode = CelSelectionModeState.NONE,
            )
        }
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(CustomColors.emptyItem)
        )
    }
}
