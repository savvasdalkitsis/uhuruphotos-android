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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.module

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.KeyScheme
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.EncryptedPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    @Provides
    @PlainTextPreferences
    fun preferences(@PlainTextPreferences prefs: FlowSharedPreferences): Preferences =
        com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.Preferences(prefs)

    @Provides
    @EncryptedPreferences
    fun encryptedPreferences(@EncryptedPreferences prefs: FlowSharedPreferences): Preferences =
        com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.Preferences(prefs)

    @Provides
    @PlainTextPreferences
    fun flowPreferences(@ApplicationContext context: Context): FlowSharedPreferences =
        FlowSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context))

    @Provides
    @EncryptedPreferences
    fun encryptedFlowPreferences(
        @EncryptedPreferences encryptedPreferences: SharedPreferences
    ): FlowSharedPreferences = FlowSharedPreferences(encryptedPreferences)

    @Provides
    @EncryptedPreferences
    fun encryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "encrypted_shared_prefs",
            MasterKey.Builder(context)
                .setKeyScheme(KeyScheme.AES256_GCM)
                .build(),
            AES256_SIV,
            AES256_GCM
        )
}