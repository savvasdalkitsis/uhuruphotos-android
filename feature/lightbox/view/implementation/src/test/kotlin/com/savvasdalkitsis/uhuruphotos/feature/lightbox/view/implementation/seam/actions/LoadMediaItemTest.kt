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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.SingleItemModel
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxMutation.ShowMedia
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.LocalIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadMediaItemTest {

    private val hash = MediaItemHashModel(Md5Hash("hash"), 1)
    private val context: LightboxActionsContext = mockk(relaxed = true) {
        every { currentMediaId } returns MutableSharedFlow(1)
    }

    private suspend fun showsShareIcon(id: MediaIdModel<*>): Boolean {
        val action = LoadMediaItem(id, hash, SingleItemModel)
        val media = with(action) { context.handle(mockk()) }.first() as ShowMedia
        return media.mediaItemStates.single().showShareIcon
    }

    private fun local(isVideo: Boolean) = LocalIdModel(1, 1, isVideo, "content://media/1", "", hash)

    private fun remote(isVideo: Boolean) = RemoteIdModel("remote", isVideo, hash)

    @Test
    fun `shows the share icon for local videos`() = runTest {
        assertTrue(showsShareIcon(local(isVideo = true)))
    }

    @Test
    fun `shows the share icon for photos`() = runTest {
        assertTrue(showsShareIcon(local(isVideo = false)))
        assertTrue(showsShareIcon(remote(isVideo = false)))
    }

    @Test
    fun `hides the share icon for videos only on the server`() = runTest {
        assertFalse(showsShareIcon(remote(isVideo = true)))
    }
}
