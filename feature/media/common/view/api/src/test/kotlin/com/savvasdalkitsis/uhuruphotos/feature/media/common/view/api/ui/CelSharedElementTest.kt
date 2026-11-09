/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CelSharedElementTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val leavingGrid = LazyGridState()
    private var showArrivingGrid by mutableStateOf(false)

    @Test
    fun `keeps laying out when a leaving grid scrolls a cel in while its match is already showing`() {
        showGrids()
        composeRule.mainClock.autoAdvance = false
        showArrivingGrid = true
        Snapshot.sendApplyNotifications()
        repeat(3) { composeRule.mainClock.advanceTimeByFrame() }

        leavingGrid.dispatchRawDelta(with(composeRule.density) { (rowHeight * arrivingFirstRow).toPx() })
        repeat(3) { composeRule.mainClock.advanceTimeByFrame() }

        assertEquals(arrivingFirstCel, leavingGrid.firstVisibleItemIndex)
    }

    private fun showGrids() {
        composeRule.setContent {
            val context = LocalContext.current
            CompositionLocalProvider(LocalThumbnailImageLoader provides remember { ImageLoader(context) }) {
                SharedTransitionLayout(Modifier.size(300.dp, 600.dp)) {
                    AnimatedContent(
                        targetState = showArrivingGrid,
                        transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
                    ) { arriving ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            when {
                                arriving -> CelGrid(rememberLazyGridState(), cels.drop(arrivingFirstCel))
                                else -> CelGrid(leavingGrid, cels)
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Composable
    private fun SharedTransitionScope.CelGrid(state: LazyGridState, cels: List<CelState>) {
        LazyVerticalGrid(columns = GridCells.Fixed(3), state = state) {
            items(cels, key = { it.mediaItem.id.value }) { cel ->
                Cel(state = cel, onSelected = {})
            }
        }
    }

    private companion object {
        const val arrivingFirstRow = 20
        const val arrivingFirstCel = arrivingFirstRow * 3
        val rowHeight = 100.dp
        val cels = (0 until 120).map { index ->
            val hash = MediaItemHashModel.fromRemoteMediaHash("hash$index", 1)
            CelState(MediaItemInstanceModel(MediaIdModel.RemoteIdModel("id$index", false, hash), hash))
        }
    }
}
