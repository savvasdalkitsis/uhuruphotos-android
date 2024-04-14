/*
Copyright 2023 Savvas Dalkitsis

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

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.icu.text.NumberFormat.getIntegerInstance
import android.icu.text.NumberFormat.getPercentInstance
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.math.toProgressPercent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ForegroundNotificationWorker<BR>(
    context: Context,
    params: WorkerParameters,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
    @StringRes private val notificationTitle: Int,
    private val notificationId: Int,
    private val notificationChannelId: String = NotificationChannels.Jobs.id,
    private val cancelBroadcastReceiver: Class<BR>? = null,
) : CoroutineWorker(context, params) where BR : BroadcastReceiver {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)

    @SuppressLint("MissingPermission")
    final override suspend fun doWork(): Result = withContext(dispatcher) {
        try {
            setForeground(getForegroundInfo())
        } catch (_: Exception) {
            // ignore
        }
        try {
            work()
        } catch (e: Exception) {
            log(e)
            Result.failure()
        } finally {
            ifNotificationsEnabled {
                notificationManager.cancel(notificationId)
            }
        }.also {
            ifNotificationsEnabled {
                getFinishedNotification(it)?.let { (id, notification) ->
                    notificationManager.notify(id, notification)
                }
            }
        }
    }

    open fun getFinishedNotification(result: Result): Pair<Int, Notification>? = null

    abstract suspend fun work(): Result

    suspend fun updateProgress(current: Int, max: Int) {
        updateProgress(current.toLong(), max.toLong())
    }

    suspend fun updateProgress(current: Long, max: Long) {
        val progress = current.toProgressPercent(max)
        val i = getIntegerInstance()
        val p = getPercentInstance()
        updateProgress(
            (100 * progress).toInt(),
            "${i.format(current)}/${i.format(max)} (${p.format(progress)})"
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun updateProgress(progress: Int, text: String? = null) {
        setProgress(workDataOf(Progress to progress))
        ifNotificationsEnabled {
            notificationManager.notify(
                notificationId, foregroundInfoBuilder.buildNotification(
                    context = applicationContext,
                    title = notificationTitle,
                    channel = notificationChannelId,
                    progress = progress,
                    text = text,
                    cancelBroadcastReceiver = cancelBroadcastReceiver,
                )
            )
        }
    }

    private fun ifNotificationsEnabled(block: () -> Unit) {
        if (notificationManager.areNotificationsEnabled()) {
            block()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = foregroundInfoBuilder.build(
        context = applicationContext,
        title = notificationTitle,
        notificationId = notificationId,
        channel = notificationChannelId,
        progress = null, // indeterminate
    )

    companion object {
        const val Progress = "Progress"
        private val dispatcher = Dispatchers.IO.limitedParallelism(5)

        fun getProgressOf(work: WorkInfo) = work.progress.getInt(Progress, 0)
        fun getProgressOrNullOf(work: WorkInfo) = getProgressOf(work).takeIf {
            work.progress.keyValueMap.containsKey(Progress)
        }
    }
}