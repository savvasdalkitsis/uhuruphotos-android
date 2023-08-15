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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun FeedMemory(
    memory: MemoryCel,
    onMemorySelected: (memory: CelState, yearsAgo: Int) -> Unit,
) {
    var index by remember {
        mutableIntStateOf(0)
    }
    Card(
        modifier = Modifier
            .padding(0.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
        ) {
            val cel = memory.cels[index]
            Crossfade(
                targetState = cel,
                animationSpec = tween(1000),
                label = "memoryCrossFade",
            ) { celState ->
                Cel(
                    modifier = Modifier
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        0f to Color.Transparent,
                                        0.5f to Color.Transparent,
                                        1f to Color.Black.copy(alpha = 0.8f),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY,
                                        tileMode = TileMode.Clamp,
                                    )
                                )
                            }
                        },
                    itemPadding = 0.dp,
                    state = celState,
                    onSelected = {
                        onMemorySelected(cel, memory.yearsAgo)
                    },
                    aspectRatio = 0.7f,
                    contentScale = ContentScale.Crop,
                    selectable = true,
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp),
                text = stringResource(string.years_ago, memory.yearsAgo),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
    LaunchedEffect(memory) {
        while(isActive) {
            delay(6000)
            index = (index + 1) % memory.cels.size
        }
    }
}