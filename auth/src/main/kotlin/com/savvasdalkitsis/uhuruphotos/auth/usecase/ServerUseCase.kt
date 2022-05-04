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
package com.savvasdalkitsis.uhuruphotos.auth.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.prefixedWithHttpsIfNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getNullableString("serverUrl")

    fun observeServerUrl(): Flow<String> = preference.asFlow()
        .map { it?.trim() }
        .filterNot { it.isNullOrEmpty() }
        .filterNotNull()

    fun getServerUrl(): String? = preference.get()?.trim()

    suspend fun setServerUrl(serverUrl: String) {
        preference.setAndCommit(serverUrl.prefixedWithHttpsIfNeeded)
    }
}