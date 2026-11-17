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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation

import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation.module.PreferencesModule
import dagger.Lazy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferencesTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private var storesOpened = 0
    private val openedStore by lazy {
        storesOpened++
        FlowSharedPreferences(sharedPreferences)
    }
    private val store = Lazy { openedStore }

    @Test
    fun `creating the encrypted preferences does not open the encrypted store`() {
        PreferencesModule().encryptedPreferences(store)

        assertEquals(0, storesOpened)
    }

    @Test
    fun `creating the plain text preferences does not open the store`() {
        PreferencesModule().preferences(store)

        assertEquals(0, storesOpened)
    }

    @Test
    fun `the store is opened once on first use and serves every read and write`() {
        every { sharedPreferences.contains("key") } returns true
        val preferences = PreferencesModule().encryptedPreferences(store)

        assertTrue(preferences.contains("key"))
        preferences.remove("key")

        assertEquals(1, storesOpened)
        verify { sharedPreferences.edit() }
    }
}
