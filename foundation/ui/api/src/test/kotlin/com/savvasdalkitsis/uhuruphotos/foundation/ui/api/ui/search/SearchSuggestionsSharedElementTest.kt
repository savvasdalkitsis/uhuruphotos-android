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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.sharedElement
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchSuggestionsSharedElementTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var showingPerson by mutableStateOf(true)

    @Test
    fun `keeps laying out when returning to a suggestion showing the person being left`() {
        showScreens()
        composeRule.mainClock.autoAdvance = false
        showingPerson = false
        Snapshot.sendApplyNotifications()
        repeat(5) { composeRule.mainClock.advanceTimeByFrame() }

        composeRule.onNodeWithText(personSuggestion.filterable).assertExists()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun showScreens() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp))) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = showingPerson,
                        transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
                    ) { person ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            when {
                                person -> PersonPicture(Modifier.size(48.dp))
                                else -> Search()
                            }
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    @Composable
    private fun SharedTransitionScope.Search() {
        SearchField(
            queryCacheKey = "search",
            latestQuery = "Ali",
            searchSuggestions = persistentListOf(personSuggestion),
            suggestionLeadingContent = persistentMapOf(
                personSuggestion.type to { PersonPicture(Modifier.fillMaxSize()) },
            ),
            onNewQuery = {},
            onSearchFor = {},
        )
    }

    @Composable
    private fun SharedTransitionScope.PersonPicture(modifier: Modifier) {
        Box(modifier.aspectRatio(1f).sharedElement(SharedElementId.personImage(personId)))
    }

    private companion object {
        const val personId = 1
        val personSuggestion = object : SearchSuggestion {
            override val filterable = "Alice"
            override val type = "person"
        }
    }
}
