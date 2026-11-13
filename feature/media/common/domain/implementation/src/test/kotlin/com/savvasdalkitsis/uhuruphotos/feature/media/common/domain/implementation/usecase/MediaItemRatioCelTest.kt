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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MediaItemRatioCelTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun show(item: MediaItemModel) {
        composeRule.setContent {
            val context = LocalContext.current
            CompositionLocalProvider(LocalThumbnailImageLoader provides remember { ImageLoader(context) }) {
                SharedTransitionLayout {
                    AnimatedContent(targetState = Unit) {
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Cel(modifier = Modifier.width(100.dp).testTag("cel"), state = item.toCel(), onSelected = {})
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cel").assertHeightIsEqualTo(100.dp)
    }

    @Test
    fun `shows a very tall server photo whose aspect ratio was rounded down to zero`() {
        show(summary(aspectRatio = 0f).toRemoteMediaItem(1, 4, null, null))
    }

    @Test
    fun `shows a device photo that reports no height`() {
        val hash = MediaItemHashModel.fromRemoteMediaHash("hash", 1)
        show(MediaItemInstanceModel(MediaIdModel.RemoteIdModel("id", false, hash), hash, ratio = localItem(height = 0).ratio))
    }

    private fun summary(aspectRatio: Float) = DbRemoteMediaItemSummary(
        id = "id",
        dominantColor = null,
        url = "hash1",
        location = null,
        date = null,
        birthTime = null,
        aspectRatio = aspectRatio,
        type = "image",
        videoLength = null,
        rating = null,
        gpsLat = null,
        gpsLon = null,
        containerId = "1",
    )

    private fun localItem(height: Int) = LocalMediaItem(
        id = 1,
        displayName = "1.jpg",
        displayDate = "",
        displayDateTime = "",
        dateTaken = "",
        dateTimeTaken = "",
        sortableDate = "",
        bucket = LocalMediaFolder(1, "Camera"),
        width = 1080,
        height = height,
        size = 1,
        contentUri = "content://1",
        thumbnailPath = null,
        md5 = Md5Hash("9e107d9d372bb6826bd81d3542a419d6"),
        video = false,
        duration = null,
        latLon = null,
        fallbackColor = null,
        path = null,
        orientation = MediaOrientation.ORIENTATION_0,
    )
}
