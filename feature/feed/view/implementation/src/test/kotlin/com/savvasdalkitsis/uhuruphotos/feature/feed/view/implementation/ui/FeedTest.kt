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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.test.core.app.ApplicationProvider
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.MissingPermissionsRequested
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.viewmodel.FeedCompositeAction
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either

import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import dev.shreyaspatil.permissionFlow.PermissionFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FeedTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val requested = mutableListOf<List<String>>()
    private val actions = mutableListOf<FeedCompositeAction>()
    private val registryOwner = object : ActivityResultRegistryOwner {
        override val activityResultRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                requested += (input as Array<*>).map { it.toString() }
            }
        }
    }
    private var state by mutableStateOf(FeedState())
    private val navigator = mockk<Navigator>(relaxed = true) {
        every { backStack } returns mutableStateListOf(FeedNavigationRoute)
    }

    @Before
    fun initialisePermissions() {
        PermissionFlow.init(context)
    }

    @Test
    fun `asks once for the permissions a local deletion was missing`() {
        state = state.copy(missingPermissions = persistentListOf(WRITE_EXTERNAL_STORAGE))
        showFeed()

        state = state.copy(isRefreshing = !state.isRefreshing)
        composeRule.waitForIdle()

        assertEquals(listOf(listOf(WRITE_EXTERNAL_STORAGE)), requested)
        assertEquals(listOf<FeedCompositeAction>(Either.Left(MissingPermissionsRequested)), actions)
    }

    @Test
    fun `shows every dialog the state asks for`() {
        state = state.copy(
            showTrashingConfirmationDialog = true,
            showDeleteConfirmationDialog = true,
            showFullySyncedDeleteConfirmationDialog = true,
            showAddToAlbumDialog = persistentListOf(),
            showNewAlbumNameDialog = true,
        )
        showFeed()

        composeRule.onAllNodes(isDialog()).assertCountEquals(5)
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun showFeed() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides registryOwner,
                LocalThumbnailImageLoader provides remember { ImageLoader(context) },
                LocalNavigator provides navigator,
                LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp)),
            ) {
                SharedTransitionLayout {
                    AnimatedContent(targetState = Unit) {
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Feed(state to AccountOverviewState()) { actions += it }
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
}
