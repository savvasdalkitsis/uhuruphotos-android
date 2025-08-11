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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.NewCelState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_chevron_double_down
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.years_ago
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun SharedTransitionScope.FeedMemory(
    memory: MemoryCelState,
    onMemorySelected: (memory: NewCelState, yearsAgo: Int) -> Unit,
    onScrollToMemory: (NewCelState) -> Unit,
    scrollState: ScrollState,
    parentWidth: Int = 0,
) {
    var index by remember {
        mutableIntStateOf(0)
    }
    var offset by remember(memory.parallaxEnabled) { mutableFloatStateOf(0f) }
    Card(
        modifier = Modifier
            .onGloballyPositioned {
                if (!memory.parallaxEnabled) {
                    return@onGloballyPositioned
                }
                val x = it.positionInParent().x
                val width = it.size.width
                val scroll = scrollState.value
                val off = if (x - scroll + width > parentWidth) {
                    max(0f, x - parentWidth - scroll + width)
                } else if (x - scroll < 0) {
                    min(0f, x - scroll)
                } else {
                    0f
                }
                offset = -off / 2
            }
            .padding(0.dp),
        shape = MaterialTheme.shapes.medium,
//        elevation = 4.dp,
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
        ) {
            memory.cels.getOrNull(index)?.let { cel ->
                Crossfade(
                    targetState = cel,
                    animationSpec = tween(1000),
                    label = "memoryCrossFade",
                ) { celState ->
                    Box(modifier = Modifier
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
                    ) {
                        Cel(
                            modifier = Modifier.scale(1.2f),
                            state = celState,
                            onSelected = {
                                onMemorySelected(cel, memory.yearsAgo)
                            },
                            aspectRatio = 0.7f,
                            contentScale = ContentScale.Crop,
                            contentOffset = offset,
                        )
                    }
                }
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                ) {
                    UhuruActionIcon(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
                        onClick = { onScrollToMemory(cel) },
                        icon = drawable.ic_chevron_double_down,
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
    }
    LaunchedEffect(memory) {
        while(isActive) {
            delay(6000)
            index = (index + 1) % memory.cels.size
        }
    }
}

@Preview
@Composable
private fun FeedMemoryPreview() {
    SharedTransitionLayout {
        PreviewAppTheme {
            FeedMemory(memory = MemoryCelState(
                yearsAgo = 10,
                cels = persistentListOf(NewCelState()),
            ), onMemorySelected = { _, _ -> }, onScrollToMemory = {}, scrollState = rememberScrollState()
            )
        }
    }

}