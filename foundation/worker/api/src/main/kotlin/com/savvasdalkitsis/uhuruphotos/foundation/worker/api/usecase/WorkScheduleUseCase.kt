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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase

import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

interface WorkScheduleUseCase {

    fun <W: CoroutineWorker> schedulePeriodic(
        workName: String,
        klass: KClass<W>,
        repeatInterval: Long,
        repeatIntervalTimeUnit: TimeUnit,
        initialDelayDuration: Long,
        initialDelayTimeUnit: TimeUnit,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelay: Long = 1,
        backoffTimeUnit: TimeUnit = TimeUnit.MINUTES,
        networkRequirement: NetworkType = NetworkType.CONNECTED,
        requiresCharging: Boolean = false,
        tags: Set<String> = emptySet(),
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
    )

    fun <W: CoroutineWorker> scheduleNow(
        workName: String,
        klass: KClass<W>,
        existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
        backoffPolicy: BackoffPolicy = BackoffPolicy.EXPONENTIAL,
        backoffDelay: Long = 1,
        backoffTimeUnit: TimeUnit = TimeUnit.MINUTES,
        networkRequirement: NetworkType = NetworkType.CONNECTED,
        requiresCharging: Boolean = false,
        tags: Set<String> = emptySet(),
        params: Data.Builder.() -> Data.Builder = { this },
    )

    fun cancelAllScheduledWork()

    fun cancelUniqueWork(workName: String)
}