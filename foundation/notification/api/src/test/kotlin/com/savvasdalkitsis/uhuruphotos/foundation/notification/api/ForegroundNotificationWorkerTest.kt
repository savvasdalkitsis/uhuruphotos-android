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
package com.savvasdalkitsis.uhuruphotos.foundation.notification.api

import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.ForegroundUpdater
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.jobs
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ForegroundNotificationWorkerTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val promotion = SettableFuture.create<Void>()
    private val params: WorkerParameters = mockk(relaxed = true) {
        every { id } returns UUID.randomUUID()
        every { foregroundUpdater } returns ForegroundUpdater { _, _, _ -> promotion }
    }
    private val foregroundInfoBuilder: ForegroundInfoBuilder = mockk {
        coEvery { build(any(), any(), any(), any(), any()) } returns ForegroundInfo(
            1,
            NotificationCompat.Builder(context, NotificationChannels.Jobs.id)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .build(),
        )
    }

    @Before
    fun setUp() {
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(NotificationChannels.Jobs.id, "jobs", NotificationManager.IMPORTANCE_LOW)
        )
    }

    @Test
    fun `retries long work that could not move to the foreground`() = runBlocking {
        promotion.setException(ForegroundServiceStartNotAllowedException("app is in the background"))

        assertEquals(Result.retry(), worker(lasting = 2.seconds).doWork())
    }

    @Test
    fun `finishes short work that could not move to the foreground`() = runBlocking {
        promotion.setException(ForegroundServiceStartNotAllowedException("app is in the background"))

        assertEquals(Result.success(), worker(lasting = Duration.ZERO).doWork())
    }

    @Test
    fun `finishes long work once it is in the foreground`() = runBlocking {
        promotion.set(null)

        assertEquals(Result.success(), worker(lasting = 500.milliseconds).doWork())
    }

    private fun worker(lasting: Duration) = object : ForegroundNotificationWorker<BroadcastReceiver>(
        context,
        params,
        foregroundInfoBuilder,
        notificationTitle = string.jobs,
        notificationId = 1,
        backgroundBudget = 100.milliseconds,
    ) {
        override suspend fun work(): Result {
            delay(lasting)
            return Result.success()
        }
    }
}
