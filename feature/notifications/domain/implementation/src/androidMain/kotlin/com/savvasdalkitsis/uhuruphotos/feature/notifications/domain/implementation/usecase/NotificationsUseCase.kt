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
package com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.usecase

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.NotificationManagerCompat
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.api.usecase.NotificationsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.initializer.NotificationActivityInitializer
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest.ACCEPTED
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class NotificationsUseCase @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    private val notificationActivityInitializer: NotificationActivityInitializer,
    @PlainTextPreferences
    private val preferences: Preferences,
) : NotificationsUseCase {

    private val keyShowNotificationsOnboarding = "show_notifications_onboarding"

    override fun needToShowNotificationsOnboardingScreen(): Boolean =
        preferences.get(keyShowNotificationsOnboarding, true) && isTiramisuOrLater() &&
                !notificationManager.areNotificationsEnabled()

    override fun neverShowNotificationsOnboardingScreenAgain() {
        preferences.set(keyShowNotificationsOnboarding, false)
    }

    override suspend fun askForPermission(): Boolean =
        if (isTiramisuOrLater()) {
            notificationActivityInitializer.askForPermission(POST_NOTIFICATIONS) == ACCEPTED
        } else {
            true
        }

    @ChecksSdkIntAtLeast(api = TIRAMISU)
    private fun isTiramisuOrLater() = SDK_INT >= TIRAMISU
}