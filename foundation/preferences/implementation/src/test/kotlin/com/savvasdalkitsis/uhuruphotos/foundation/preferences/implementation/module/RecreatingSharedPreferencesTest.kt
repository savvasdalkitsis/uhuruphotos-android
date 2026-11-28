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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException
import javax.crypto.AEADBadTagException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RecreatingSharedPreferencesTest {

    private val context = RuntimeEnvironment.getApplication()
    private val store = context.getSharedPreferences("store", Context.MODE_PRIVATE)
    private val discarded = mutableListOf<String>()

    private fun opening(vararg failures: Exception): SharedPreferences {
        val remaining = failures.toMutableList()
        return RecreatingSharedPreferences(
            create = { remaining.removeFirstOrNull()?.let { throw it } ?: store },
            discard = { discarded += "store" },
        ).open()
    }

    @Test
    fun `recreates a store whose keys came from another device`() {
        assertSame(store, opening(AEADBadTagException("keyset does not decrypt")))
        assertEquals(listOf("store"), discarded)
    }

    @Test
    fun `recreates a store whose keyset is corrupt`() {
        assertSame(store, opening(IOException("keyset does not parse")))
        assertEquals(listOf("store"), discarded)
    }

    @Test
    fun `keeps a store that opens`() {
        assertSame(store, opening())
        assertEquals(emptyList<String>(), discarded)
    }

    @Test
    fun `discarding the encrypted store removes its file`() {
        val prefs = context.getSharedPreferences(ENCRYPTED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        prefs.edit().putString("auth:keyUserName", "transferred").commit()

        discardEncryptedPreferences(context)

        assertEquals(false, File(context.filesDir.parentFile, "shared_prefs/$ENCRYPTED_PREFERENCES_FILE.xml").exists())
        assertNull(
            context.getSharedPreferences(ENCRYPTED_PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getString("auth:keyUserName", null)
        )
    }
}
