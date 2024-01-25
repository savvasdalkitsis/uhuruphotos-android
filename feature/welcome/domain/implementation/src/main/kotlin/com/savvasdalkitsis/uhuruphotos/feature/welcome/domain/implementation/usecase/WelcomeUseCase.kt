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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.model.WelcomeStatus
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class WelcomeUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
) : WelcomeUseCase {

    private val keySeenScreen = "seenWelcomeScreen"

    override suspend fun needToShowWelcomeScreen(): Boolean =
        !preferences.get(keySeenScreen, false) || observeWelcomeStatus().first().allMissing

    override suspend fun getWelcomeStatus(): WelcomeStatus = observeWelcomeStatus().first()

    override fun observeWelcomeStatus(): Flow<WelcomeStatus> = combine(
        localMediaUseCase.observePermissionsState(),
        authenticationUseCase.observeRefreshToken(),
    ) { permissions, refreshToken ->
        WelcomeStatus(permissions, refreshToken)
    }

    override fun markWelcomeScreenSeen() {
        preferences.set(keySeenScreen, true)
    }
}