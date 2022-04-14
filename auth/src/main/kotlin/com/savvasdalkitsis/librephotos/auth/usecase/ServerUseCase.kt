package com.savvasdalkitsis.librephotos.auth.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.librephotos.db.extensions.read
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ServerUseCase @Inject constructor(
    preferences: FlowSharedPreferences,
) {
    private val preference = preferences.getNullableString("serverUrl")

    @Volatile
    private var serverUrlCache: String? = null

    suspend fun getServerUrl(): String? = serverUrlCache
        ?: read { preference.get()?.trim().also { serverUrlCache = it } }

    suspend fun setServerUrl(serverUrl: String) {
        preference.setAndCommit(serverUrl)
        serverUrlCache = serverUrl
    }
}