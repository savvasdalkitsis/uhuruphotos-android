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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import io.mockk.mockk
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AutoAlbumsSharedElementTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var showingAlbums by mutableStateOf(false)
    private val libraryTiles = mutableMapOf<String, SharedContentState>()

    @Test
    fun `grows the auto albums library tile into the auto albums screen`() {
        showLibraryThenAlbums()

        assertEquals(
            mapOf(autoAlbums to true, userAlbums to false),
            libraryTiles.mapValues { (_, tile) -> tile.isMatchFound },
        )
    }

    private fun showLibraryThenAlbums() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalNavigator provides remember { mockk(relaxed = true) }) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = showingAlbums,
                        transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
                    ) { albums ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            when {
                                albums -> AutoAlbums(state = AutoAlbumsState(), action = {})
                                else -> LibraryTiles(this)
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.mainClock.autoAdvance = false
        showingAlbums = true
        Snapshot.sendApplyNotifications()
        repeat(5) { composeRule.mainClock.advanceTimeByFrame() }
    }

    @Composable
    private fun SharedTransitionScope.LibraryTiles(scope: AnimatedVisibilityScope) {
        Box {
            for (key in listOf(autoAlbums, userAlbums)) {
                val tile = rememberSharedContentState(key)
                libraryTiles[key] = tile
                Box(Modifier.size(100.dp).sharedElement(tile, scope))
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    private companion object {
        val autoAlbums = SharedElementId.autoAlbumsCanvas().value
        val userAlbums = SharedElementId.userAlbumsCanvas().value
    }
}
