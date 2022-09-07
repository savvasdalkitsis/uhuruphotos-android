package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker

import android.content.Context
import androidx.work.Data
import androidx.work.DelegatingWorkerFactory
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.ProgressUpdater
import androidx.work.WorkerParameters
import androidx.work.impl.utils.SerialExecutorImpl
import androidx.work.impl.utils.futures.SettableFuture
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import androidx.work.workDataOf
import com.google.common.util.concurrent.Futures
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
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

class FeedDownloadWorkerTest {

    private val feedRepository = mockk<FeedRepository>(relaxed = true)
    private val foregroundInfoBuilder = mockk<ForegroundInfoBuilder>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private fun feedDownloadWorker(params: WorkerParameters = params()): FeedDownloadWorker {
        return FeedDownloadWorker(
            context,
            params,
            feedRepository,
            foregroundInfoBuilder
        )
    }

    @Test
    fun `starts a shallow refresh`() = runBlocking {
        val underTest = feedDownloadWorker(params {
            put(
                FeedDownloadWorker.KEY_SHALLOW,
                true
            )
        })

        underTest.doWork()

        coVerify { feedRepository.refreshRemoteMediaCollections(eq(true), any()) }
    }

    @Test
    fun `starts a deep refresh`() = runBlocking {
        val underTest = feedDownloadWorker(params {
            put(
                FeedDownloadWorker.KEY_SHALLOW,
                false
            )
        })

        underTest.doWork()

        coVerify { feedRepository.refreshRemoteMediaCollections(eq(false), any()) }
    }

    @Test
    fun `provides updates on progress`() = runBlocking {
        val progressUpdater = mockk<ProgressUpdater>()
        val underTest = feedDownloadWorker(
            params(
                progressUpdater = progressUpdater
            )
        )
        val progress = slot<suspend (Int) -> Unit>()
        coEvery { feedRepository.refreshRemoteMediaCollections(any(), capture(progress)) }
        every {
            progressUpdater.updateProgress(
                any(),
                any(),
                any()
            )
        } returns Futures.immediateFuture(mockk())

        underTest.doWork()

        progress.captured(5)

        verify {
            progressUpdater.updateProgress(
                any(),
                any(),
                workDataOf(FeedDownloadWorker.Progress to 5)
            )
        }
    }

    @Test
    fun `returns success result when completing`() = runBlocking {
        val underTest = feedDownloadWorker()

        assert(underTest.doWork() == ListenableWorker.Result.success())
    }

    @Test
    fun `returns retry result when refreshing errors`() = runBlocking {
        val underTest = feedDownloadWorker()
        coEvery { feedRepository.refreshRemoteMediaCollections(any(), any()) } returns
                Result.failure(IllegalStateException())

        assert(underTest.doWork() == ListenableWorker.Result.retry())
    }

    @Test
    fun `provides foreground info`() = runBlocking {
        val underTest = feedDownloadWorker()
        val foregroundInfo = mockk<ForegroundInfo>()
        every {
            foregroundInfoBuilder.build(
                context,
                R.string.refreshing_feed,
                any(),
                NotificationChannels.JOBS_CHANNEL_ID
            )
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

            override fun getSerialTaskExecutor() = SerialExecutorImpl(executor)
        },
        DelegatingWorkerFactory(),
        progressUpdater
    ) { _, _, _ -> SettableFuture.create() }
}