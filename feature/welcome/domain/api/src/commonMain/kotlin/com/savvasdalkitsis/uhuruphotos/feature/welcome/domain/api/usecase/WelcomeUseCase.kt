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
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.model.WelcomeStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

interface WelcomeUseCase {

    suspend fun needToShowWelcomeScreen(): Boolean
    fun markWelcomeScreenSeen()
    fun observeWelcomeStatus(): Flow<WelcomeStatus>
    suspend fun getWelcomeStatus(): WelcomeStatus
}

fun <T> WelcomeUseCase.flow(
    withRemoteAccess: Flow<T>,
    withoutRemoteAccess: Flow<T>,
) = observeWelcomeStatus().flatMapLatest { welcomeStatus ->
    when {
        welcomeStatus.hasRemoteAccess -> withRemoteAccess
        else -> withoutRemoteAccess
    }
}

suspend fun <T> WelcomeUseCase.get(
    withRemoteAccess: suspend () -> T,
    withoutRemoteAccess: suspend () -> T,
): T {
    val welcomeStatus = getWelcomeStatus()
    return when {
        welcomeStatus.hasRemoteAccess -> withRemoteAccess()
        else -> withoutRemoteAccess()
    }
}