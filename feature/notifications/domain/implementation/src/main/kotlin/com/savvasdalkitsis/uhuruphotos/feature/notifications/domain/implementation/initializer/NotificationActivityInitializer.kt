package com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.initializer

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    override fun onActivityCreated(activity: ComponentActivity) {
        launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            notificationRequestState.tryEmit(if (accepted) ACCEPTED else DENIED)
        }
    }

    override fun onActivityDestroyed(activity: ComponentActivity) {}

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