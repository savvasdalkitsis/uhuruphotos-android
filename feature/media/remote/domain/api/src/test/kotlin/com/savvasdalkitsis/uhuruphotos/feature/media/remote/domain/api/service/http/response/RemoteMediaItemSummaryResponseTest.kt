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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response

import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteMediaItemSummaryResponseTest {

    private val moshi = Moshi.Builder().build()
    private val summaryAdapter = moshi.adapter(RemoteMediaItemSummaryResponse::class.java)
    private val containerAdapter = moshi.adapter(RemoteMediaItemSummaryContainerResponseData::class.java)
    private val detailsAdapter = moshi.adapter(RemoteMediaItemResponseData::class.java)

    private val uuid = "0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e"
    private val imageHash = "9e107d9d372bb6826bd81d3542a419d61"

    private val summaryJson = """
        {"id": "$uuid", "image_hash": "$imageHash", "dominantColor": "#112233",
        "url": "$imageHash", "location": "", "date": "2024-05-01T10:00:00Z",
        "birthTime": "2024-05-01T10:00:00Z", "aspectRatio": 1.5, "type": "image",
        "video_length": "", "rating": 0, "owner": {"id": 1, "username": "admin"},
        "exif_gps_lat": 52.5, "exif_gps_lon": 13.4, "removed": false, "in_trashcan": false,
        "archived": false, "trashed_at": null, "stacks": [], "has_raw_variant": false}
    """.trimIndent()

    @Test
    fun `parses the summary of current servers and keeps uuid and content hash apart`() {
        val summary = summaryAdapter.fromJson(summaryJson)!!

        assertEquals(uuid, summary.id)
        assertEquals(imageHash, summary.contentHash)
        assertEquals("52.5", summary.lat)
        assertEquals(imageHash, summary.toDbModel("20240501").url)
        assertEquals(uuid, summary.toTrash("20240501").id)
    }

    @Test
    fun `content hash falls back to url and then id`() {
        val withUrl = RemoteMediaItemSummaryResponse(id = uuid, url = imageHash, date = null)
        val bare = RemoteMediaItemSummaryResponse(id = imageHash, date = null)

        assertEquals(imageHash, withUrl.contentHash)
        assertEquals(imageHash, bare.contentHash)
    }

    @Test
    fun `tolerates redacted fields`() {
        val summary = summaryAdapter.fromJson(
            """{"id": "$uuid", "date": null, "location": null, "dominantColor": null, "rating": null, "owner": null}"""
        )!!

        assertNull(summary.location)
        assertEquals(0, summary.toDbModel("1").rating)
        assertEquals(0, summary.toTrash("1").rating)
    }

    @Test
    fun `container reads day_id of current servers`() {
        val container = containerAdapter.fromJson(
            """{"photo_summary": $summaryJson, "day_id": 20240501, "processing": false}"""
        )!!

        assertEquals("20240501", container.containerId)
    }

    @Test
    fun `container reads album_date_id of 1_5 servers`() {
        val container = containerAdapter.fromJson(
            """{"photo_summary": $summaryJson, "album_date_id": 42, "processing": false}"""
        )!!

        assertEquals("42", container.containerId)
    }

    @Test
    fun `details keep the photo id next to the content hash`() {
        val details = detailsAdapter.fromJson(
            """{"id": "$uuid", "image_hash": "$imageHash", "video": false, "rating": 3}"""
        )!!.toDbModel()

        assertEquals(imageHash, details.imageHash)
        assertEquals(uuid, details.remoteId)
    }
}
