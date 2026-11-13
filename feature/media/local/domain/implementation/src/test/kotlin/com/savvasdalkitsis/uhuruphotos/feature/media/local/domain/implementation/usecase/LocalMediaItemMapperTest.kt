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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.joda.time.format.DateTimeFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMediaItemMapperTest {

    private val dateDisplayer: DateDisplayer = mockk {
        coEvery { dateString(any()) } returns "date"
        coEvery { dateTimeString(any()) } returns "dateTime"
    }
    private val underTest = LocalMediaItemMapper(
        localMediaDateTimeFormat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss"),
        parsingDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"),
        parsingDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd"),
        dateDisplayer = dateDisplayer,
    )

    private fun details(
        latLon: String? = null,
        orientation: String? = null,
        id: Long = 7,
        bucketName: String = "Camera",
    ) = LocalMediaItemDetails(
        id = id,
        displayName = "photo.jpg",
        dateTaken = "2024/05/06 07:08:09",
        bucketId = 540528482,
        bucketName = bucketName,
        width = 4,
        height = 3,
        size = 12,
        contentUri = "content://media/external/images/media/7",
        md5 = "md5",
        video = false,
        duration = null,
        latLon = latLon,
        fallbackColor = null,
        path = null,
        orientation = orientation,
        thumbnailPath = null,
    )

    @Test
    fun `maps dates and folder of a local media item`() = runTest {
        val item = underTest.toItem(details())

        assertEquals("2024-05-06", item.dateTaken)
        assertEquals("2024-05-06 07:08:09", item.dateTimeTaken)
        assertEquals("date", item.displayDate)
        assertEquals(LocalMediaFolder(540528482, "Camera"), item.bucket)
    }

    @Test
    fun `maps a valid location`() = runTest {
        assertEquals(1.5 to -2.5, underTest.toItem(details(latLon = "1.5,-2.5")).latLon)
    }

    @Test
    fun `drops a location that cannot be parsed`() = runTest {
        assertNull(underTest.toItems(listOf(details(latLon = "1.5,north"))).single().latLon)
        assertNull(underTest.toItem(details(latLon = "1.5")).latLon)
    }

    @Test
    fun `maps every known orientation`() = runTest {
        val orientations = listOf("0", "90", "180", "270", "45", null).map {
            underTest.toItem(details(orientation = it)).orientation
        }

        assertEquals(
            listOf(
                MediaOrientation.ORIENTATION_0,
                MediaOrientation.ORIENTATION_90,
                MediaOrientation.ORIENTATION_180,
                MediaOrientation.ORIENTATION_270,
                MediaOrientation.ORIENTATION_UNKNOWN,
                MediaOrientation.ORIENTATION_UNKNOWN,
            ),
            orientations,
        )
    }

    @Test
    fun `groups items of a bucket into one folder even when their folder names differ`() = runTest {
        val folders = underTest.toFolders(
            listOf(details(id = 1, bucketName = "Camera"), details(id = 2, bucketName = "camera")),
        )

        val camera = LocalMediaFolder(540528482, "Camera")
        assertEquals(setOf(camera), folders.keys)
        assertEquals(listOf(camera, camera), folders.getValue(camera).map { it.bucket })
    }
}
