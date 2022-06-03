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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.http.prefixedWithHttpsIfNeeded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) : ServerUseCase {
    private val preference = preferences.getNullableString("serverUrl")

    override fun observeServerUrl(): Flow<String> = preference.asFlow()
        .map { it?.trim() }
        .filterNot { it.isNullOrEmpty() }
        .filterNotNull()

    override fun getServerUrl(): String? = preference.get()?.trim()

    override suspend fun setServerUrl(serverUrl: String) {
        preference.setAndCommit(serverUrl.prefixedWithHttpsIfNeeded)
    }
}