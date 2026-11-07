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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteMediaCollectionsFilterModelTest {

    @Test
    fun `timeline leaves archived photos out like the web timeline`() {
        assertEquals(mapOf("archived" to "false"), RemoteMediaCollectionsFilterModel.Timeline.toQuery())
    }

    @Test
    fun `each filter maps to the day list query parameter`() {
        assertEquals(mapOf("favorite" to "true"), RemoteMediaCollectionsFilterModel.Favourites.toQuery())
        assertEquals(mapOf("hidden" to "true"), RemoteMediaCollectionsFilterModel.Hidden.toQuery())
        assertEquals(mapOf("in_trashcan" to "true"), RemoteMediaCollectionsFilterModel.Trash.toQuery())
        assertEquals(mapOf("person" to "12"), RemoteMediaCollectionsFilterModel.person(12).toQuery())
    }

    @Test
    fun `modified since only reaches the legacy query`() {
        val filter = RemoteMediaCollectionsFilterModel.Timeline.copy(modifiedSince = "2026-01-01T00:00:00Z")

        assertEquals(mapOf("archived" to "false"), filter.toQuery())
        assertEquals(
            mapOf("archived" to "false", "last_modified" to "2026-01-01T00:00:00Z"),
            filter.toLegacyQuery(),
        )
    }

    @Test
    fun `legacy query without modified since is the plain query`() {
        assertEquals(mapOf("favorite" to "true"), RemoteMediaCollectionsFilterModel.Favourites.toLegacyQuery())
    }
}
