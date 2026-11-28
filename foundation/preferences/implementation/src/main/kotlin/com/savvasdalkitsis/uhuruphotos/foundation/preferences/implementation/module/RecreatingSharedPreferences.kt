/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.module

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.MasterKey
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore

internal const val ENCRYPTED_PREFERENCES_FILE = "encrypted_shared_prefs"

internal fun discardEncryptedPreferences(context: Context) {
    context.deleteSharedPreferences(ENCRYPTED_PREFERENCES_FILE)
    runCatching {
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            .deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
    }
}

internal class RecreatingSharedPreferences(
    private val create: () -> SharedPreferences,
    private val discard: () -> Unit,
) {

    fun open(): SharedPreferences = try {
        create()
    } catch (e: GeneralSecurityException) {
        recreate(e)
    } catch (e: IOException) {
        recreate(e)
    }

    private fun recreate(cause: Exception): SharedPreferences {
        log(cause) { "Encrypted preferences cannot be opened, recreating them" }
        discard()
        return create()
    }
}
