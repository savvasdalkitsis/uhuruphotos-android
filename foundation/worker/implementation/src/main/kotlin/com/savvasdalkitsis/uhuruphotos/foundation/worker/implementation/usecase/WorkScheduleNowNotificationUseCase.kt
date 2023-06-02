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

import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleNowNotificationUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleNowUseCase
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass

@AutoBind
@ActivityRetainedScoped
class WorkScheduleNowNotificationUseCase @Inject constructor(
    private val workScheduleNowUseCase: WorkScheduleNowUseCase,
    private val notificationManager: NotificationManagerCompat,
    private val workScheduleNotificationActivityInitializer: WorkScheduleNotificationActivityInitializer,
) : WorkScheduleNowNotificationUseCase {

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
        if (!notificationManager.areNotificationsEnabled()) {
            workScheduleNotificationActivityInitializer.requestPermission()
        }
        workScheduleNowUseCase.scheduleNow(
            workName,
            klass,
            existingWorkPolicy,
            backoffPolicy,
            backoffDelay,
            backoffTimeUnit,
            networkRequirement,
            params
        )
    }

}