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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.ForegroundInfo
import androidx.work.ForegroundUpdater
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import androidx.work.workDataOf
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UploadPostCompletionWorkerTest {

    private val mediaUseCase: RemoteMediaUseCase = mockk()
    private val uploadUseCase: UploadUseCase = mockk(relaxed = true)
    private val feedUseCase: FeedUseCase = mockk {
        coEvery { refreshCluster("day") } returns Ok(Unit)
    }
    private val promotions = mutableListOf<ForegroundInfo>()
    private val foregroundUpdater = ForegroundUpdater { _, _, info ->
        promotions += info
        SettableFuture.create<Void>().apply { set(null) }
    }

    @Test
    fun `checks an uploaded item without starting a foreground service`() = runTest {
        RuntimeEnvironment.getApplication().getSystemService(NotificationManager::class.java)
            .createNotificationChannel(
                NotificationChannel(NotificationChannels.Jobs.id, "jobs", NotificationManager.IMPORTANCE_LOW)
            )
        coEvery { mediaUseCase.getRemoteMediaItemSummary("hash") } returns
            Ok(RemoteMediaItemSummaryStatus.Found("day"))

        assertEquals(Result.success(), worker(attempt = 0).doWork())
        assertEquals(emptyList<ForegroundInfo>(), promotions)
    }

    @Test
    fun `retries a post upload check that fails within a week`() = runTest {
        coEvery { mediaUseCase.getRemoteMediaItemSummary("hash") } returns Err(IOException("offline"))

        assertEquals(Result.retry(), worker(attempt = 300).doWork())
    }

    @Test
    fun `gives up on a post upload check that keeps failing for a week`() = runTest {
        coEvery { mediaUseCase.getRemoteMediaItemSummary("hash") } returns Err(IOException("offline"))

        assertEquals(Result.failure(), worker(attempt = 347).doWork())
        verify { uploadUseCase.markAsNotProcessing(7) }
    }

    @Test
    fun `gives up on an item that is still processing after a week`() = runTest {
        coEvery { mediaUseCase.getRemoteMediaItemSummary("hash") } returns
            Ok(RemoteMediaItemSummaryStatus.Processing(mockk(relaxed = true)))

        assertEquals(Result.failure(), worker(attempt = 347).doWork())
        verify { uploadUseCase.markAsNotProcessing(7) }
    }

    private fun worker(attempt: Int) = UploadPostCompletionWorker(
        RuntimeEnvironment.getApplication(),
        mockk<WorkerParameters>(relaxed = true) {
            every { runAttemptCount } returns attempt
            every { foregroundUpdater } returns this@UploadPostCompletionWorkerTest.foregroundUpdater
            every { inputData } returns workDataOf(
                UploadPostCompletionWorker.KEY_ITEM_ID to 7L,
                UploadPostCompletionWorker.KEY_HASH to "hash",
            )
        },
        mediaUseCase,
        feedUseCase,
        uploadUseCase,
    )
}
