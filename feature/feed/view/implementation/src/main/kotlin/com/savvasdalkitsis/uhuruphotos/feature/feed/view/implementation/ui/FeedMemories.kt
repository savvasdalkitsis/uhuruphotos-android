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

package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.NewCelState
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.on_this_day

@Composable
internal fun SharedTransitionScope.FeedMemories(
    memories: List<MemoryCelState>,
    onScrollToMemory: (NewCelState) -> Unit,
    onMemorySelected: (memory: NewCelState, yearsAgo: Int) -> Unit,
) {
    var width by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .onGloballyPositioned {
                width = it.size.width
            },
        verticalArrangement = spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                top = 16.dp,
                bottom = 16.dp,
            ),
            text = stringResource(string.on_this_day),
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        )
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState),
            horizontalArrangement = spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(0.dp))
            for (memory in memories) {
                FeedMemory(
                    memory = memory,
                    onMemorySelected = onMemorySelected,
                    onScrollToMemory = onScrollToMemory,
                    scrollState = scrollState,
                    parentWidth = width,
                )
            }
            Spacer(modifier = Modifier.width(0.dp))
        }
    }
}