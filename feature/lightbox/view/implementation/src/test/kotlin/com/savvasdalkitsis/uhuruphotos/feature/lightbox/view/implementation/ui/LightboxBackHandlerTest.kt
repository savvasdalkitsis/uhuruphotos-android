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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.core.SnapSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableContentLocation
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LightboxBackHandlerTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<LightboxAction>()
    private var leftLightbox = false
    private lateinit var zoomableState: ZoomableState
    private lateinit var scope: CoroutineScope

    @Before
    fun listenForLeavingTheLightbox() {
        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    leftLightbox = true
                }
            })
        }
    }

    private fun showImage(size: Float, maxZoom: Float = 2f) {
        composeRule.setContent {
            zoomableState = rememberZoomableState(ZoomSpec(maxZoomFactor = maxZoom))
            scope = rememberCoroutineScope()
            Box(Modifier.size(300.dp).zoomable(zoomableState))
            LaunchedEffect(Unit) {
                zoomableState.setContentLocation(ZoomableContentLocation.unscaledAndTopLeftAligned(Size(size, size)))
            }
            LightboxBackHandler(
                isTrulyActivePage = true,
                showingActionsOverlay = true,
                scrollState = rememberScrollState(),
                zoomableState = zoomableState,
                action = { actions += it },
            )
        }
        composeRule.waitForIdle()
    }

    private fun pressBack() {
        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `leaves the lightbox on back for an image that is scaled up to fit the screen`() {
        showImage(size = 200f)
        assertEquals(1.5f, zoomableState.contentTransformation.scale.scaleX)
        assertEquals(0f, zoomableState.zoomFraction)

        pressBack()

        assertTrue(leftLightbox)
    }

    @Test
    fun `leaves the lightbox on back for an image too small to zoom into`() {
        showImage(size = 30f)
        assertEquals(10f, zoomableState.contentTransformation.scale.scaleX)

        pressBack()

        assertTrue(leftLightbox)
    }

    @Test
    fun `resets the zoom on back when the image is zoomed in`() {
        showImage(size = 200f)
        scope.launch { zoomableState.zoomTo(zoomFactor = 3f, animationSpec = SnapSpec()) }
        composeRule.waitForIdle()
        assertEquals(1f, zoomableState.zoomFraction)

        pressBack()

        assertFalse(leftLightbox)
        assertEquals(emptyList<LightboxAction>(), actions)
        assertEquals(0f, zoomableState.zoomFraction)
    }

    @Test
    fun `leaves the lightbox on back for an image scaled up to the maximum zoom`() {
        showImage(size = 100f, maxZoom = 3f)
        assertEquals(3f, zoomableState.contentTransformation.scale.scaleX)

        pressBack()

        assertTrue(leftLightbox)
    }

    @Test
    fun `resets the zoom on back when a scaled up image is zoomed in all the way`() {
        showImage(size = 206f, maxZoom = 3f)
        scope.launch { zoomableState.zoomTo(zoomFactor = 3f, animationSpec = SnapSpec()) }
        composeRule.waitForIdle()
        assertEquals(1f, zoomableState.zoomFraction)

        pressBack()

        assertFalse(leftLightbox)
        assertEquals(0f, zoomableState.zoomFraction)
    }
}