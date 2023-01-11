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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.api

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkScheduler @Inject constructor(
    val workManager: WorkManager,
) {
    inline fun <reified W: CoroutineWorker> scheduleNow(
        workName: String,
        existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelay: Long = 1,
        backoffTimeUnit: TimeUnit = TimeUnit.MINUTES,
        networkRequirement: NetworkType = NetworkType.CONNECTED,
        params: Data.Builder.() -> Data.Builder = { this },
    ) {
        workManager.enqueueUniqueWork(
            workName,
            existingWorkPolicy,
            OneTimeWorkRequestBuilder<W>()
                .setInputData(params(Data.Builder()).build())
                .setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(networkRequirement)
                    .build())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build(),
        )
    }

    inline fun <reified W: CoroutineWorker> schedulePeriodic(
        workName: String,
        repeatInterval: Long,
        repeatIntervalTimeUnit: TimeUnit,
        initialDelayDuration: Long,
        initialDelayTimeUnit: TimeUnit,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelay: Long = 1,
        backoffTimeUnit: TimeUnit = TimeUnit.MINUTES,
        networkRequirement: NetworkType = NetworkType.CONNECTED,
        requiresCharging: Boolean = false,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
    ) {
        workManager.enqueueUniquePeriodicWork(
            workName,
            existingPeriodicWorkPolicy,
            PeriodicWorkRequestBuilder<W>(repeatInterval, repeatIntervalTimeUnit)
                .setInitialDelay(initialDelayDuration, initialDelayTimeUnit)
                .setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(networkRequirement)
                    .setRequiresCharging(requiresCharging)
                    .build())
                .build(),
        )
    }

    fun cancelAllScheduledWork() {
        workManager.cancelAllWork()
    }

    fun cancelWork(workName: String) {
        workManager.cancelUniqueWork(workName)
    }
}