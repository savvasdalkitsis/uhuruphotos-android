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
package com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import androidx.test.core.app.ApplicationProvider
import dev.shreyaspatil.permissionFlow.PermissionFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MissingPermissionsRequestTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val requested = mutableListOf<List<String>>()
    private var missingPermissions by mutableStateOf(listOf<String>())
    private var recompositionTrigger by mutableStateOf(0)
    private var requests = 0
    private val registryOwner = object : ActivityResultRegistryOwner {
        override val activityResultRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?,
            ) {
                requested += (input as Array<*>).map { it.toString() }
            }
        }
    }

    @Before
    fun showRequest() {
        PermissionFlow.init(ApplicationProvider.getApplicationContext())
        composeRule.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registryOwner) {
                recompositionTrigger
                MissingPermissionsRequest(missingPermissions) {
                    requests++
                    missingPermissions = emptyList()
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun `does not ask when nothing is missing`() {
        assertEquals(emptyList<List<String>>(), requested)
        assertEquals(0, requests)
    }

    @Test
    fun `asks once for the missing permissions`() {
        missingPermissions = listOf(WRITE_EXTERNAL_STORAGE)
        composeRule.waitForIdle()
        recompositionTrigger++
        composeRule.waitForIdle()

        assertEquals(listOf(listOf(WRITE_EXTERNAL_STORAGE)), requested)
        assertEquals(1, requests)
    }

    @Test
    fun `asks again when permissions go missing again`() {
        missingPermissions = listOf(WRITE_EXTERNAL_STORAGE)
        composeRule.waitForIdle()
        missingPermissions = listOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
        composeRule.waitForIdle()

        assertEquals(
            listOf(listOf(WRITE_EXTERNAL_STORAGE), listOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)),
            requested,
        )
        assertEquals(2, requests)
    }
}
