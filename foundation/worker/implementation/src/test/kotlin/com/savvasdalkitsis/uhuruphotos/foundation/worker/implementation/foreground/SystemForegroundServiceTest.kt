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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.foreground

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.Configuration
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.impl.foreground.SystemForegroundDispatcher
import androidx.work.impl.foreground.SystemForegroundService
import androidx.work.impl.model.WorkGenerationalId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowService

@RunWith(RobolectricTestRunner::class)
class SystemForegroundServiceTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val service by lazy { Robolectric.buildService(SystemForegroundService::class.java).create().get() }
    private val shadow by lazy { shadowOf(service) }

    @Before
    fun setUp() {
        if (!WorkManager.isInitialized()) {
            WorkManager.initialize(context, Configuration.Builder().build())
        }
    }

    @Test
    fun `stays alive for a foreground start that arrives while the previous worker shuts it down`() {
        service.onStartCommand(startIntent("finishing", 1), 0, 1)
        val next = notification()

        service.onStartCommand(SystemForegroundDispatcher.createStopForegroundIntent(context), 0, 2)

        assertFalse(shadow.systemStopsService(latestStartId = 3))
        service.onStartCommand(startIntent("starting", 2, next), 0, 3)
        assertSame(next, shadow.lastForegroundNotification)
    }

    @Test
    fun `stops when the last foreground worker finishes`() {
        service.onStartCommand(startIntent("finishing", 1), 0, 1)

        service.onStartCommand(SystemForegroundDispatcher.createStopForegroundIntent(context), 0, 2)

        assertTrue(shadow.systemStopsService(latestStartId = 2))
    }

    private fun ShadowService.systemStopsService(latestStartId: Int) =
        isStoppedBySelf && stopSelfId !in 1 until latestStartId

    private fun startIntent(workSpecId: String, notificationId: Int, notification: Notification = notification()): Intent =
        SystemForegroundDispatcher.createStartForegroundIntent(
            context,
            WorkGenerationalId(workSpecId, 0),
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC),
        )

    private fun notification(): Notification = NotificationCompat.Builder(context, "jobs")
        .setSmallIcon(android.R.drawable.stat_notify_sync)
        .build()
}
