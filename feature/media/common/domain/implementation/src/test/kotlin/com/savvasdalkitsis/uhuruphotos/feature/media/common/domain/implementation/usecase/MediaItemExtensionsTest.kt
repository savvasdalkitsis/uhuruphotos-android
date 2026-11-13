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

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_0
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemExtensionsTest {

    @Test
    fun `a local item keeps its proportions`() {
        assertEquals(2f, item(width = 200, height = 100).ratio)
    }

    @Test
    fun `a rotated local item swaps its proportions`() {
        assertEquals(0.5f, item(width = 200, height = 100, orientation = ORIENTATION_90).ratio)
    }

    @Test
    fun `a local item without a height is shown square`() {
        assertEquals(1f, item(width = 200, height = 0).ratio)
    }

    @Test
    fun `a local item without dimensions is shown square`() {
        assertEquals(1f, item(width = 0, height = 0).ratio)
    }

    private fun item(width: Int, height: Int, orientation: MediaOrientation = ORIENTATION_0) = LocalMediaItem(
        id = 1,
        displayName = "1.jpg",
        displayDate = "",
        displayDateTime = "",
        dateTaken = "",
        dateTimeTaken = "",
        sortableDate = "",
        bucket = LocalMediaFolder(1, "Camera"),
        width = width,
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
        orientation = orientation,
    )
}
