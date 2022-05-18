package com.savvasdalkitsis.uhuruphotos.albums.initializer

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.uhuruphotos.albums.worker.AlbumWorkScheduler
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