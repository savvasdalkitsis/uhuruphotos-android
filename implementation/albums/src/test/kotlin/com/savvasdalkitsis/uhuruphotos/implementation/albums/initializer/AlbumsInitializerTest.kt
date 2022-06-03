/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.initializer

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.implementation.albums.initializer.AlbumsInitializer
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AlbumsInitializerTest {

    @Test
    fun `schedules periodic album sync on app startup`() {
        val albumWorkScheduler = mockk<AlbumWorkScheduler>(relaxed = true)
        val underTest = AlbumsInitializer(albumWorkScheduler)

        underTest.onAppCreated(Application())

        verify { albumWorkScheduler.scheduleAlbumsRefreshPeriodic(ExistingPeriodicWorkPolicy.KEEP) }
    }
}