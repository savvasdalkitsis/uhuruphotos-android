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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.ImageLoader
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.LocalAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AutoAlbumActionsContextTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val albumId = 7
    private val galleryDetails = slot<(Int) -> Flow<GalleryDetailsState>>()
    private val galleryActionsContextFactory: GalleryActionsContextFactory = mockk {
        every { create(any(), capture(galleryDetails), any(), any(), any(), any(), any()) } returns mockk()
    }

    private fun details(vararg entries: GetAutoAlbum): GalleryDetailsState {
        AutoAlbumActionsContext(
            autoAlbumUseCase = mockk {
                every { observeAutoAlbumWithPeople(albumId) } returns flowOf(entries.toList() to emptyList())
            },
            serverUseCase = mockk { every { getServerUrl() } returns "https://server" },
            dateDisplayer = mockk { coEvery { dateString(any()) } answers { firstArg<String?>().orEmpty() } },
            galleryActionsContextFactory = galleryActionsContextFactory,
            userUseCase = mockk { coEvery { getRemoteUserOrRefresh() } returns Ok(mockk { every { id } returns 1 }) },
            navigator = mockk(),
            toaster = mockk(),
        )
        return runBlocking { galleryDetails.captured(albumId).first() }
    }

    private fun entry(photoId: String?, timestamp: String?) = GetAutoAlbum(
        id = albumId.toString(),
        title = "Trip",
        albumTimestamp = "2026-01-01",
        createdOn = null,
        isFavorite = null,
        gpsLat = null,
        gpsLon = null,
        photoId = photoId,
        photoRemoteId = photoId,
        rating = null,
        video = false,
        timestamp = timestamp,
    )

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun show(details: GalleryDetailsState) {
        composeRule.setContent {
            ComposeResourcesContext()
            val context = LocalContext.current
            CompositionLocalProvider(
                LocalThumbnailImageLoader provides remember { ImageLoader(context) },
                LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp)),
                LocalAnimatedVideoThumbnails provides false,
            ) {
                SharedTransitionLayout {
                    AnimatedContent(targetState = Unit) {
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Collage(state = CollageState(clusters = details.clusterStates))
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

    @Test
    fun `shows an event whose photos were deleted from the server`() {
        val details = details(
            entry("kept", "2026-01-01"),
            entry(null, null),
            entry(null, null),
        )

        show(details)

        assertEquals(
            listOf(listOf("kept")),
            details.clusterStates.map { cluster -> cluster.cels.map { it.mediaItem.id.value } },
        )
    }

    @Test
    fun `keeps the title of an event without photos`() {
        val details = details(entry(null, null))

        assertEquals(Title.Text("Trip"), details.title)
        assertEquals(emptyList<Any>(), details.clusterStates)
    }
}
