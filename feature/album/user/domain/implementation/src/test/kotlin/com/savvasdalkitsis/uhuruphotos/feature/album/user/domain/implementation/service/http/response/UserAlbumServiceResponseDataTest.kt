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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.service.http.request.UserAlbumEditModelRequestData
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserAlbumServiceResponseDataTest {

    private val moshi = Moshi.Builder().build()

    @Test
    fun `parses an album whose undated group has a null date`() {
        val album = moshi.adapter(UserAlbumServiceResponseData::class.java).fromJson(
            """{"id": 3, "title": "Trip", "date": "", "location": "",
                "grouped_photos": [
                    {"date": "2024-05-01", "location": "", "items": [{"id": "a", "date": "2024-05-01T10:00:00Z"}]},
                    {"date": null, "location": "", "items": [{"id": "b", "date": ""}]}
                ]}"""
        )!!

        assertEquals(listOf("a", "b"), album.groups.flatMap { it.items }.map { it.id })
        assertNull(album.groups[1].date)
    }

    @Test
    fun `adding photos does not send the title`() {
        val json = moshi.adapter(UserAlbumEditModelRequestData::class.java)
            .toJson(UserAlbumEditModelRequestData(ids = listOf("a")))

        assertEquals("""{"photos":["a"]}""", json)
    }
}
