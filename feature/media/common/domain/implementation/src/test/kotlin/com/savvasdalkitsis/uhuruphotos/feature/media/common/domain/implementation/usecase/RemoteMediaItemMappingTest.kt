/*
Copyright 2024 Savvas Dalkitsis

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

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteMediaItemMappingTest {

    private val md5 = "9e107d9d372bb6826bd81d3542a419d6"
    private val uuid = "0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e"
    private val localHash = MediaItemHashModel(Md5Hash(md5), 1)

    @Test
    fun `a summary matches its local copy by content hash and keeps the uuid for the api`() {
        val item = summary(url = "${md5}1", rating = 4).toRemoteMediaItem(1, 4, "May 1", null)

        assertEquals(localHash, item.mediaHash)
        assertEquals(RemoteIdModel(uuid, false, localHash), item.id)
        assertTrue(item.isFavourite)
        assertEquals("May 1", item.displayDayDate)
        assertEquals(1.5f, item.ratio)
    }

    @Test
    fun `a summary without rating or threshold is not a favourite`() {
        assertFalse(summary(url = "${md5}1", rating = null).toRemoteMediaItem(1, 4, null, null).isFavourite)
        assertFalse(summary(url = "${md5}1", rating = 5).toRemoteMediaItem(1, null, null, null).isFavourite)
    }

    @Test
    fun `a collection item matches its local copy by content hash`() {
        val item = source(hash = "${md5}1").toRemoteMediaItem(1, 4, "May 1", null)!!

        assertEquals(localHash, item.mediaHash)
        assertEquals(uuid, item.id.value)
        assertEquals(52.5 to 13.4, item.latLng)
        assertEquals(1f, item.ratio)
    }

    @Test
    fun `a collection item without hash falls back to its id`() {
        val item = source(hash = null).toRemoteMediaItem(1, 4, null, null)!!

        assertEquals(MediaItemHashModel.fromRemoteMediaHash(uuid, 1), item.mediaHash)
    }

    @Test
    fun `a collection row without a photo is skipped`() {
        assertNull(source(id = null).toRemoteMediaItem(1, 4, null, null))
        assertNull(source(id = "").toRemoteMediaItem(1, 4, null, null))
    }

    @Test
    fun `a collection item needs both coordinates for a location`() {
        assertNull(source(lon = null).toRemoteMediaItem(1, 4, null, null)!!.latLng)
        assertNull(source(lat = "x").toRemoteMediaItem(1, 4, null, null)!!.latLng)
    }

    @Test
    fun `a summary whose aspect ratio was rounded down to zero is shown square`() {
        assertEquals(1f, summary(url = "${md5}1", rating = null, aspectRatio = 0f).toRemoteMediaItem(1, 4, null, null).ratio)
    }

    @Test
    fun `a collection item whose aspect ratio was rounded down to zero is shown square`() {
        assertEquals(1f, source(aspectRatio = 0f).toRemoteMediaItem(1, 4, null, null)!!.ratio)
    }

    private fun summary(url: String, rating: Int?, aspectRatio: Float = 1.5f) = DbRemoteMediaItemSummary(
        id = uuid,
        dominantColor = "#000000",
        url = url,
        location = null,
        date = "2024-05-01T10:00:00Z",
        birthTime = null,
        aspectRatio = aspectRatio,
        type = "image",
        videoLength = null,
        rating = rating,
        gpsLat = null,
        gpsLon = null,
        containerId = "20240501",
    )

    private fun source(
        id: String? = uuid,
        hash: String? = "${md5}1",
        lat: String? = "52.5",
        lon: String? = "13.4",
        aspectRatio: Float? = null,
    ) = MediaCollectionSourceModel(
        id = "20240501",
        date = "2024-05-01T10:00:00Z",
        location = null,
        mediaItemId = id,
        mediaItemHash = hash,
        dominantColor = null,
        rating = 5,
        aspectRatio = aspectRatio,
        isVideo = false,
        lat = lat,
        lon = lon,
    )
}
