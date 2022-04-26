package com.savvasdalkitsis.uhuruphotos.auth.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.db.extensions.read
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

    suspend fun getServerUrl(): String? = read { preference.get()?.trim() }

    suspend fun setServerUrl(serverUrl: String) {
        preference.setAndCommit(serverUrl)
    }
}