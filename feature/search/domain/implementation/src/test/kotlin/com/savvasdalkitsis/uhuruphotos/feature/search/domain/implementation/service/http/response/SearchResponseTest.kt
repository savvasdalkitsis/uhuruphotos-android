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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.http.response

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.model.SearchHitModel
import com.squareup.moshi.Moshi
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchResponseTest {

    private val adapter = Moshi.Builder().build().adapter(SearchResultsResponseData::class.java)

    @Test
    fun `reads date groups`() {
        val results = adapter.fromJson(
            """{"results": [
                {"date": "2024-05-01", "location": "Berlin", "items": [{"id": "a", "date": "2024-05-01T10:00:00Z"}]},
                {"date": "No timestamp", "location": "", "items": [{"id": "b", "date": ""}]}
            ]}"""
        )!!

        assertEquals(
            listOf(
                SearchHitModel("a", "2024-05-01", "Berlin"),
                SearchHitModel("b", "No timestamp", ""),
            ),
            results.results.flatMap { it.toSearchHits() },
        )
    }

    @Test
    fun `reads the flat list of semantic search`() {
        val results = adapter.fromJson(
            """{"results": [
                {"id": "a", "date": "2024-05-01T10:00:00Z", "location": "Berlin", "type": "image"},
                {"id": "b", "date": "", "location": "", "type": "image"}
            ]}"""
        )!!

        assertEquals(
            listOf(
                SearchHitModel("a", "2024-05-01", "Berlin"),
                SearchHitModel("b", "No timestamp", ""),
            ),
            results.results.flatMap { it.toSearchHits() },
        )
    }

    @Test
    fun `a group without date or an entry without id is tolerated`() {
        assertEquals(
            listOf(SearchHitModel("a", "No timestamp", null)),
            SearchResponse(items = listOf(summary("a"))).toSearchHits(),
        )
        assertEquals(emptyList<SearchHitModel>(), SearchResponse().toSearchHits())
        assertEquals(listOf(SearchHitModel("c", "No timestamp", null)), SearchResponse(id = "c").toSearchHits())
    }

    private fun summary(id: String) = RemoteMediaItemSummaryResponse(id = id, date = null)
}
