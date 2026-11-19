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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.usecase.HeatMapUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(RobolectricTestRunner::class)
class CameraViewPortChangedTest {

    private val berlin = LatLon(52.5, 13.4)
    private val munich = LatLon(48.1, 11.6)
    private val aroundBerlin = Viewport(berlin, 10f)
    private val aroundMunich = Viewport(munich, 10f)
    private val savingBerlin = CountDownLatch(1)
    private val berlinSaveMayFinish = CountDownLatch(1)
    private val saved = CopyOnWriteArrayList<Viewport>()
    private val hash = mockk<MediaItemHashModel>(relaxed = true)
    private val media = listOf(berlin, munich).mapIndexed { index, location ->
        MediaItemInstanceModel(RemoteIdModel("$index", false, hash), hash, latLng = location.lat to location.lon)
    }
    private val heatMapUseCase = mockk<HeatMapUseCase> {
        every { observeViewport() } returns flowOf(aroundBerlin)
        every { setViewport(any()) } answers {
            if (firstArg<Viewport>() == aroundBerlin) {
                savingBerlin.countDown()
                berlinSaveMayFinish.await(5, SECONDS)
            }
            saved += firstArg<Viewport>()
        }
    }
    private val feedUseCase = mockk<FeedUseCase> {
        every { observeFeed(any(), any()) } returns flowOf(listOf(MediaCollectionModel("all", media, "")))
    }
    private val mediaUseCase = mockk<MediaUseCase> { every { observeLocalMedia() } returns emptyFlow() }
    private val context = HeatMapActionsContext(
        feedUseCase, mediaUseCase, mockk(), mockk(), mockk(), heatMapUseCase, mockk(),
    )
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val seam = Seam(ActionHandlerWithContext(context), HeatMapState(), scope)

    @After
    fun tearDown() {
        berlinSaveMayFinish.countDown()
        scope.cancel()
    }

    @Test
    fun `a slow earlier viewport never overrides a later one`() = runBlocking {
        seam.action(Load)
        withTimeout(5000) { seam.state.first { it.allMedia.size == 2 } }

        seam.action(CameraViewPortChanged(aroundBerlin) { it == berlin })
        savingBerlin.await(5, SECONDS)
        seam.action(CameraViewPortChanged(aroundMunich) { it == munich })
        withTimeoutOrNull(1000) { while (aroundMunich !in saved) delay(10) }
        berlinSaveMayFinish.countDown()
        withTimeout(5000) { while (saved.size < 2) delay(10) }
        delay(500)

        assertEquals(listOf(aroundBerlin, aroundMunich), saved.toList())
        assertEquals(listOf(munich), seam.state.value.pointsOnVisibleMap.toList())
    }
}
