/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.KeyScheme
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.Preferences

object PreferencesModule {

    val plainTextPreferences: com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
        get() = Preferences(plainTextFlowSharedPreferences)

    val encryptedPreferences: com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
        get() = Preferences(encryptedFlowSharedPreferences)

    private val plainTextFlowSharedPreferences: FlowSharedPreferences
        get() = FlowSharedPreferences(
            PreferenceManager.getDefaultSharedPreferences(AndroidModule.applicationContext)
        )

    private val encryptedFlowSharedPreferences: FlowSharedPreferences
        get() = FlowSharedPreferences(encryptedSharedPreferences)

    private val encryptedSharedPreferences: SharedPreferences by singleInstance {
        EncryptedSharedPreferences.create(
            AndroidModule.applicationContext,
            "encrypted_shared_prefs",
            MasterKey.Builder(AndroidModule.applicationContext)
                .setKeyScheme(KeyScheme.AES256_GCM)
                .build(),
            AES256_SIV,
            AES256_GCM
        )
    }
}