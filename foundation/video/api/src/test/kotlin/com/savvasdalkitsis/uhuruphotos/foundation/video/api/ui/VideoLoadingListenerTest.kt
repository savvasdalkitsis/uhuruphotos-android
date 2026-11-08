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
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui

import androidx.media3.common.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class VideoLoadingListenerTest {

    private val events = mutableListOf<String>()
    private val underTest = VideoLoadingListener(
        onReady = { events += "ready" },
        onFinishedLoading = { events += "finished loading" },
    )

    @Test
    fun `reports the video as ready only when playback is ready`() {
        underTest.onPlaybackStateChanged(Player.STATE_BUFFERING)
        underTest.onPlaybackStateChanged(Player.STATE_READY)

        assertEquals(listOf("ready"), events)
    }

    @Test
    fun `reports the video as loaded only when loading stops`() {
        underTest.onIsLoadingChanged(true)
        underTest.onIsLoadingChanged(false)

        assertEquals(listOf("finished loading"), events)
    }
}
