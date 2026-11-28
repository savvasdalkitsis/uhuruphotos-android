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
package com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.initializer

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowDownloadManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DownloadActivityInitializerTest {

    private val app = RuntimeEnvironment.getApplication()
    private val downloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val localMediaWorkScheduler = mockk<LocalMediaWorkScheduler>(relaxed = true)
    private val activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    private val underTest = DownloadActivityInitializer(downloadManager, localMediaWorkScheduler)

    @After
    fun tearDown() {
        underTest.onActivityDestroyed(activity)
    }

    private fun download(status: Int): Long {
        val request = DownloadManager.Request(Uri.parse("https://photos.example/media/1"))
        val id = downloadManager.enqueue(request)
        Shadow.extract<ShadowDownloadManager.ShadowRequest>(request).setStatus(status)
        return id
    }

    private fun completeDownload(id: Long) {
        activity.sendBroadcast(
            Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE).putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, id)
        )
        shadowOf(activity.mainLooper).idle()
    }

    @Test
    fun `listens to download completions the download provider sends from its own app`() {
        underTest.onActivityCreated(activity)

        val receiver = shadowOf(app).registeredReceivers.single {
            it.intentFilter.hasAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        }

        assertEquals(Context.RECEIVER_EXPORTED, receiver.flags and Context.RECEIVER_EXPORTED)
    }

    @Test
    fun `scans local media when one of its downloads succeeds`() {
        underTest.onActivityCreated(activity)

        completeDownload(download(DownloadManager.STATUS_SUCCESSFUL))

        verify { localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning() }
    }

    @Test
    fun `ignores completions of downloads it did not start or that failed`() {
        underTest.onActivityCreated(activity)
        val failed = download(DownloadManager.STATUS_FAILED)

        completeDownload(failed)
        completeDownload(failed + 100)
        completeDownload(-1)

        verify(exactly = 0) { localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning() }
    }
}
