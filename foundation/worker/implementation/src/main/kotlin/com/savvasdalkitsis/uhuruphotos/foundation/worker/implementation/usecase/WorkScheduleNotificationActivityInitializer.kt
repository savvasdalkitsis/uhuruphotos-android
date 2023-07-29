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

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.NotificationManagerCompat
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityRetainedScoped
class WorkScheduleNotificationActivityInitializer @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
) : ActivityCreated {

    override fun priority(): Int = -1

    override fun onActivityCreated(activity: ComponentActivity) {
        if (SDK_INT >= TIRAMISU && !notificationManager.areNotificationsEnabled()) {
            activity.registerForActivityResult(RequestPermission()) {}
                .launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onActivityDestroyed(activity: ComponentActivity) {}
}