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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.ref.WeakReference

@RunWith(RobolectricTestRunner::class)
class SmartGridRetentionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private class Payload(val name: String)

    private var payloads by mutableStateOf(emptyList<Payload>())

    @Test
    fun `keeps the items of at most one earlier update inside a shared transition layout`() {
        composeRule.setContent {
            SharedTransitionLayout {
                SmartGrid(gridState = rememberSmartGridState(staggered = true), columns = 3) {
                    for (payload in payloads) {
                        item(payload.name) {
                            Box(Modifier.size(10.dp))
                            payload.name
                        }
                    }
                }
            }
        }
        val earlier = mutableListOf<WeakReference<Payload>>()
        repeat(UPDATES) { update ->
            val next = List(ITEMS) { Payload("$it") }
            if (update < UPDATES - 1) {
                next.forEach { earlier += WeakReference(it) }
            }
            composeRule.runOnIdle { payloads = next }
            composeRule.waitForIdle()
        }

        assertTrue("retained ${earlier.retained()} items", earlier.retained() <= ITEMS)
    }

    private fun List<WeakReference<Payload>>.retained(): Int {
        repeat(5) {
            System.gc()
            System.runFinalization()
            Thread.sleep(20)
        }
        return count { it.get() != null }
    }

    private companion object {
        const val UPDATES = 20
        const val ITEMS = 30
    }
}
