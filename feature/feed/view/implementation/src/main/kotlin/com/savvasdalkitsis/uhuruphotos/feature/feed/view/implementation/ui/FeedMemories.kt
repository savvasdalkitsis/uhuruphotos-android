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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun FeedMemories(
    memories: List<MemoryCel>,
    onScrollToMemory: (CelState) -> Unit,
    onMemorySelected: (memory: CelState, yearsAgo: Int) -> Unit,
) {
    Column(
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
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(0.dp))
            for (memory in memories) {
                FeedMemory(memory, onMemorySelected, onScrollToMemory)
            }
            Spacer(modifier = Modifier.width(0.dp))
        }
    }
}