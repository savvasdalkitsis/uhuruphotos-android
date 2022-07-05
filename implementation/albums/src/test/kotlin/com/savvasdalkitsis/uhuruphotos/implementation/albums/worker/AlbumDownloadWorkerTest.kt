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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.worker

import android.content.Context
import androidx.work.Data
import androidx.work.DelegatingWorkerFactory
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker.Result
import androidx.work.ProgressUpdater
import androidx.work.WorkerParameters
import androidx.work.impl.utils.SerialExecutor
import androidx.work.impl.utils.futures.SettableFuture
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import androidx.work.workDataOf
import com.google.common.util.concurrent.Futures.immediateFuture
import com.savvasdalkitsis.uhuruphotos.api.notification.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.api.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.implementation.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.implementation.albums.worker.AlbumDownloadWorker.Companion.Progress
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*
import java.util.concurrent.Executors

class AlbumDownloadWorkerTest {

    private val albumsRepository = mockk<AlbumsRepository>(relaxed = true)
    private val foregroundInfoBuilder = mockk<ForegroundInfoBuilder>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private fun albumDownloadWorker(params: WorkerParameters = params()): AlbumDownloadWorker {
        return AlbumDownloadWorker(context, params, albumsRepository, foregroundInfoBuilder)
    }

    @Test
    fun `starts a shallow refresh`() = runBlocking {
        val underTest = albumDownloadWorker(params {
            put(AlbumDownloadWorker.KEY_SHALLOW, true)
        })

        underTest.doWork()

        coVerify { albumsRepository.refreshAlbums(eq(true), any()) }
    }

    @Test
    fun `starts a deep refresh`() = runBlocking {
        val underTest = albumDownloadWorker(params {
            put(AlbumDownloadWorker.KEY_SHALLOW, false)
        })

        underTest.doWork()

        coVerify { albumsRepository.refreshAlbums(eq(false), any()) }
    }

    @Test
    fun `provides updates on progress`() = runBlocking {
        val progressUpdater = mockk<ProgressUpdater>()
        val underTest = albumDownloadWorker(
            params(
            progressUpdater = progressUpdater
        )
        )
        val progress = slot<suspend (Int) -> Unit>()
        coEvery { albumsRepository.refreshAlbums(any(), capture(progress)) }
        every { progressUpdater.updateProgress(any(), any(), any()) } returns immediateFuture(mockk())

        underTest.doWork()

        progress.captured(5)

        verify { progressUpdater.updateProgress(any(), any(), workDataOf(Progress to 5)) }
    }

    @Test
    fun `returns success result when completing`() = runBlocking {
        val underTest = albumDownloadWorker()

        assert(underTest.doWork() == Result.success())
    }

    @Test
    fun `returns retry result when refreshing errors`() = runBlocking {
        val underTest = albumDownloadWorker()
        coEvery { albumsRepository.refreshAlbums(any(), any()) } throws IllegalStateException()

        assert(underTest.doWork() == Result.retry())
    }

    @Test
    fun `provides foreground info`() = runBlocking {
        val underTest = albumDownloadWorker()
        val foregroundInfo = mockk<ForegroundInfo>()
        every {
            foregroundInfoBuilder.build(context, R.string.refreshing_albums, any(), JOBS_CHANNEL_ID)
        } returns foregroundInfo

        assert(underTest.getForegroundInfo() === foregroundInfo)
    }
}

private fun params(
    progressUpdater: ProgressUpdater = ProgressUpdater { _, _, _ -> SettableFuture.create() },
    args: Data.Builder.() -> Data.Builder = { this },
): WorkerParameters {
    val executor = Executors.newSingleThreadExecutor()
    return WorkerParameters(
        UUID.randomUUID(),
        args(Data.Builder()).build(),
        emptyList(),
        WorkerParameters.RuntimeExtras(),
        0,
        executor,
        object : TaskExecutor {
            override fun getMainThreadExecutor() = executor

            override fun getSerialTaskExecutor() = SerialExecutor(executor)
        },
        DelegatingWorkerFactory(),
        progressUpdater
    ) { _, _, _ -> SettableFuture.create() }
}