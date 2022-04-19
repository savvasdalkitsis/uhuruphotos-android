package com.savvasdalkitsis.librephotos.auth.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.librephotos.db.extensions.read
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ServerUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getNullableString("serverUrl")

    @Volatile
    private var serverUrlCache: String? = null

    fun observeServerUrl(): Flow<String> = preference.asFlow()
        .map { it?.trim() }
        .filterNot { it.isNullOrEmpty() }
        .filterNotNull()

    suspend fun getServerUrl(): String? = serverUrlCache
        ?: read { preference.get()?.trim().also { serverUrlCache = it } }

    suspend fun setServerUrl(serverUrl: String) {
        preference.setAndCommit(serverUrl)
        serverUrlCache = serverUrl
    }
}