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
import java.lang.reflect.Modifier

class MediaIdModelTest {

    private val hash = MediaItemHashModel(Md5Hash("9e107d9d372bb6826bd81d3542a419d6"), 1)
    private val remote = MediaIdModel.RemoteIdModel("0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e", false, hash)
    private val local = MediaIdModel.LocalIdModel(5, 1, false, "content://media/5", "file:///5.jpg", hash)

    @Test
    fun `names each kind of media id for list keys`() {
        assertEquals("remote:0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e", remote.serializableId)
        assertEquals("local:5", local.serializableId)
        assertEquals("downloading:0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e", remote.toDownloading().serializableId)
        assertEquals("uploading:5", local.toUploading().serializableId)
        assertEquals("processing:5", local.toProcessing().serializableId)
        assertEquals(
            "group:remote:0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e,local:5",
            MediaIdModel.GroupIdModel(listOf(remote, local), false, hash).serializableId,
        )
    }

    @Test
    fun `keeps only its own data in every remote and local id of the library`() {
        assertEquals(setOf("value", "isVideo", "mediaHash"), storedFields(MediaIdModel.RemoteIdModel::class.java))
        assertEquals(
            setOf("value", "folderId", "isVideo", "contentUri", "thumbnailUri", "mediaHash"),
            storedFields(MediaIdModel.LocalIdModel::class.java),
        )
    }

    private fun storedFields(type: Class<*>): Set<String> = generateSequence(type) { it.superclass }
        .flatMap { it.declaredFields.asSequence() }
        .filterNot { Modifier.isStatic(it.modifiers) }
        .map { it.name }
        .toSet()
}
