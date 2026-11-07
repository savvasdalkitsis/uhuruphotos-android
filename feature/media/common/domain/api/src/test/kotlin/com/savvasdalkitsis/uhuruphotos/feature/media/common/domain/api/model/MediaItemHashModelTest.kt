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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaItemHashModelTest {

    private val md5 = "9e107d9d372bb6826bd81d3542a419d6"
    private val uuid = "0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e"

    @Test
    fun `splits an image hash into md5 and owner`() {
        assertEquals(MediaItemHashModel(Md5Hash(md5), 12), MediaItemHashModel.fromRemoteMediaHash("${md5}12", 3))
    }

    @Test
    fun `a remote image hash matches the local file of its owner`() {
        assertEquals(MediaItemHashModel(Md5Hash(md5), 1), MediaItemHashModel.fromRemoteMediaHash("${md5}1", 1))
    }

    @Test
    fun `keeps the owner of a photo shared by someone else`() {
        assertEquals("${md5}7", MediaItemHashModel.fromRemoteMediaHash("${md5}7", 1).hash)
    }

    @Test
    fun `values that are not image hashes keep the old behaviour`() {
        assertEquals(MediaItemHashModel(Md5Hash("abc"), 0), MediaItemHashModel.fromRemoteMediaHash("abc0", 0))
        assertEquals(MediaItemHashModel(Md5Hash(uuid), 1), MediaItemHashModel.fromRemoteMediaHash(uuid, 1))
    }

    @Test
    fun `a hash with non alphanumeric md5 part is not split`() {
        val hash = "9e107d9d-72bb6826bd81d3542a419d6123"

        assertEquals(MediaItemHashModel(Md5Hash(hash), null), MediaItemHashModel.fromRemoteMediaHash(hash, null))
    }
}
