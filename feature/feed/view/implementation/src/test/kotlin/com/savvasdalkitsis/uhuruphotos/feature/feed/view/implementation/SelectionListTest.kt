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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Test

class SelectionListTest {

    private val hash = MediaItemHashModel(Md5Hash("hash"), userId = 1)
    private val ids = (1..2000).map { RemoteIdModel("$it", isVideo = false, mediaHash = hash) }
    private val underTest = SelectionList()

    @Test
    fun `selecting from many threads at once keeps every selection`() = runBlocking {
        withContext(Dispatchers.Default) {
            ids.map { launch { underTest.select(it) } }.joinAll()
        }

        assertEquals(ids.toSet(), underTest.ids.first())
    }

    @Test
    fun `deselecting from many threads at once drops every selection`() = runBlocking {
        ids.forEach { underTest.select(it) }

        withContext(Dispatchers.Default) {
            ids.map { launch { underTest.deselect(it) } }.joinAll()
        }

        assertEquals(emptySet<RemoteIdModel>(), underTest.ids.first())
    }
}
