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
package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.EditAction
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions.ImageCropped
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import io.mockk.mockk
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class EditTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actions = mutableListOf<EditAction>()
    private val photoUri = Uri.parse("content://$AUTHORITY/photo.png")

    @Before
    fun servePhoto() {
        imageReleased = CountDownLatch(1)
        photo = File.createTempFile("photo", ".png").apply {
            outputStream().use {
                Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
                    .apply { eraseColor(Color.RED) }
                    .compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
        Robolectric.setupContentProvider(SlowPhotoProvider::class.java, AUTHORITY)
    }

    @Test
    fun `crops the photo once it is shown even if crop was pressed while it loaded`() {
        showEdit()

        composeRule.onNodeWithContentDescription("Crop").assertIsNotEnabled().performClick()
        imageReleased.countDown()
        composeRule.waitUntil(5_000) { composeRule.onAllNodes(isEnabledCrop()).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onNodeWithContentDescription("Crop").performClick()
        awaitCrop()

        assertEquals(240, actions.filterIsInstance<ImageCropped>().single().image.width)
    }

    private fun awaitCrop() {
        repeat(50) {
            if (actions.isEmpty()) {
                composeRule.waitForIdle()
                Thread.sleep(20)
                composeRule.mainClock.advanceTimeBy(100)
            }
        }
    }

    private fun isEnabledCrop() = hasContentDescription("Crop") and isEnabled()

    private fun showEdit() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalNavigator provides remember { mockk(relaxed = true) }) {
                Edit(
                    state = EditState(
                        photoUri = photoUri,
                        fileName = "photo.png",
                        actionBarActionsEnabled = true,
                        isLoading = false,
                    ),
                    action = { actions += it },
                )
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

    class SlowPhotoProvider : ContentProvider() {
        override fun onCreate() = true
        override fun query(uri: Uri, p: Array<out String>?, s: String?, a: Array<out String>?, o: String?) = null
        override fun getType(uri: Uri) = "image/png"
        override fun insert(uri: Uri, values: ContentValues?) = null
        override fun delete(uri: Uri, s: String?, a: Array<out String>?) = 0
        override fun update(uri: Uri, v: ContentValues?, s: String?, a: Array<out String>?) = 0
        override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
            imageReleased.await(5, TimeUnit.SECONDS)
            return ParcelFileDescriptor.open(photo, ParcelFileDescriptor.MODE_READ_ONLY)
        }
    }

    private companion object {
        const val AUTHORITY = "com.savvasdalkitsis.uhuruphotos.test.photos"
        lateinit var imageReleased: CountDownLatch
        lateinit var photo: File
    }
}
