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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass

class WorkScheduleUseCase @Inject constructor(
    private val workManager: WorkManager,
) : WorkScheduleUseCase {

    override fun <W: CoroutineWorker> scheduleNow(
        workName: String,
        klass: KClass<W>,
        existingWorkPolicy: ExistingWorkPolicy,
        backoffPolicy: BackoffPolicy,
        backoffDelay: Long,
        backoffTimeUnit: TimeUnit,
        networkRequirement: NetworkType,
        params: Data.Builder.() -> Data.Builder,
    ) {
        workManager.enqueueUniqueWork(
            workName,
            existingWorkPolicy,
            OneTimeWorkRequest.Builder(klass.java)
                .setInputData(params(Data.Builder()).build())
                .setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(networkRequirement)
                    .build())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build(),
        )
    }

    override fun <W: CoroutineWorker> schedulePeriodic(
        workName: String,
        klass: KClass<W>,
        repeatInterval: Long,
        repeatIntervalTimeUnit: TimeUnit,
        initialDelayDuration: Long,
        initialDelayTimeUnit: TimeUnit,
        backoffPolicy: BackoffPolicy,
        backoffDelay: Long,
        backoffTimeUnit: TimeUnit,
        networkRequirement: NetworkType,
        requiresCharging: Boolean,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
    ) {
        workManager.enqueueUniquePeriodicWork(
            workName,
            existingPeriodicWorkPolicy,
            PeriodicWorkRequest.Builder(klass.java, repeatInterval, repeatIntervalTimeUnit)
                .setInitialDelay(initialDelayDuration, initialDelayTimeUnit)
                .setBackoffCriteria(backoffPolicy, backoffDelay, backoffTimeUnit)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(networkRequirement)
                    .setRequiresCharging(requiresCharging)
                    .build())
                .build(),
        )
    }

    override fun cancelAllScheduledWork() {
        workManager.cancelAllWork()
    }

    override fun cancelUniqueWork(workName: String) {
        workManager.cancelUniqueWork(workName)
    }
}