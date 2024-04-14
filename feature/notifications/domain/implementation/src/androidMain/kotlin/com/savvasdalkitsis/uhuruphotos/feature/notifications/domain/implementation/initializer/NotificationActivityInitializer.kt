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
package com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.initializer

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest.ACCEPTED
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest.DENIED
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest.IDLE
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ActivityCreated
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
@ActivityRetainedScoped
class NotificationActivityInitializer @Inject constructor(
) : ActivityCreated {

    private var notificationRequestState = MutableStateFlow(IDLE)
    private lateinit var launcher: ActivityResultLauncher<String>

    override fun priority(): Int = -1

    override fun onActivityCreated(activity: FragmentActivity) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            notificationRequestState.tryEmit(if (accepted) ACCEPTED else DENIED)
        }
    }

    override fun onActivityDestroyed(activity: FragmentActivity) {}

    suspend fun askForPermission(permission: String): NotificationRequest {
        launcher.launch(permission)
        return notificationRequestState.first { it != IDLE }.also {
            resetNotificationRequestState()
        }
    }

    private fun resetNotificationRequestState() {
        notificationRequestState = MutableStateFlow(IDLE)
    }
}
