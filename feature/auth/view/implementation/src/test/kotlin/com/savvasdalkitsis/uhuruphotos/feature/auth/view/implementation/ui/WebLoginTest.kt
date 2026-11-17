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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui

import android.util.AndroidRuntimeException
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WebLoginTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @After
    fun restoreCookieManager() {
        unmockkStatic(CookieManager::class)
    }

    @Test
    fun `shows an error instead of crashing when the system WebView cannot load`() {
        mockkStatic(CookieManager::class)
        every { CookieManager.getInstance() } throws AndroidRuntimeException(
            "android.webkit.WebViewFactory\$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed"
        )

        showWebLogin()

        composeRule.onNodeWithText("Could not log in").assertExists()
    }

    @Test
    fun `loads the login page in a javascript enabled WebView`() {
        showWebLogin()

        val webView = composeRule.runOnIdle { webViews().single() }
        assertTrue(webView.settings.javaScriptEnabled)
        assertEquals(true, webView.settings.userAgentString?.contains("Mobile Safari"))
    }

    @Test
    fun `recreates the WebView when the system kills its renderer`() {
        showWebLogin()
        val dead = composeRule.runOnIdle { webViews().single() }

        val handled = composeRule.runOnIdle { rendererGone(dead, crashed = false) }

        val alive = composeRule.runOnIdle { webViews().single() }
        assertTrue(handled)
        assertNotSame(dead, alive)
        assertTrue(alive.settings.javaScriptEnabled)
    }

    @Test
    fun `shows an error when the WebView renderer crashes`() {
        showWebLogin()

        val handled = composeRule.runOnIdle { rendererGone(webViews().single(), crashed = true) }

        assertTrue(handled)
        composeRule.onNodeWithText("Could not log in").assertExists()
        assertEquals(emptyList<WebView>(), composeRule.runOnIdle { webViews() })
    }

    private fun rendererGone(webView: WebView, crashed: Boolean): Boolean =
        shadowOf(webView).webViewClient.onRenderProcessGone(
            webView,
            mockk<RenderProcessGoneDetail> { every { didCrash() } returns crashed },
        )

    private fun webViews(): List<WebView> =
        composeRule.activity.window.decorView.withDescendants().filterIsInstance<WebView>()

    private fun View.withDescendants(): List<View> = listOf(this) + when (this) {
        is ViewGroup -> (0 until childCount).flatMap { getChildAt(it).withDescendants() }
        else -> emptyList()
    }

    private fun showWebLogin() {
        composeRule.setContent {
            ComposeResourcesContext()
            WebLogin(WebLoginState(url = "https://photos.example.com/login"))
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
