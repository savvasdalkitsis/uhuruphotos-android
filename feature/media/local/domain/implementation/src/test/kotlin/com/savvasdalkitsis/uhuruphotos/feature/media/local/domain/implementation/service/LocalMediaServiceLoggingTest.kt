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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service

import android.app.Application
import android.provider.MediaStore
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.FakeMediaStoreProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import saschpe.log4k.Log
import saschpe.log4k.Logger
import java.util.concurrent.CopyOnWriteArrayList
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log as LogSwitch

@RunWith(RobolectricTestRunner::class)
class LocalMediaServiceLoggingTest {

    private val context: Application = RuntimeEnvironment.getApplication()
    private val mediaStore = Robolectric.setupContentProvider(FakeMediaStoreProvider::class.java, MediaStore.AUTHORITY)
    private val logged = CopyOnWriteArrayList<String>()
    private val previousLoggers = Log.loggers.toList()
    private val wasEnabled = LogSwitch.enabled

    @Before
    fun captureLogs() {
        LogSwitch.enabled = true
        Log.loggers.clear()
        Log.loggers += object : Logger() {
            override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
                logged += message.orEmpty()
            }
        }
    }

    @After
    fun restoreLogs() {
        Log.loggers.clear()
        Log.loggers += previousLoggers
        LogSwitch.enabled = wasEnabled
    }

    @Test
    fun `logs a media store query once instead of once per row`() = runBlocking {
        mediaStore.existingIds += 7

        LocalMediaService(context, context.contentResolver).exists(7, video = false)

        assertEquals(logged.toString(), 2, logged.size)
    }
}
