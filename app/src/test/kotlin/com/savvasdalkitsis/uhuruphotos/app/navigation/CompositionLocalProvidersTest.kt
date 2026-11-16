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
package com.savvasdalkitsis.uhuruphotos.app.navigation

import android.app.Application
import android.content.ComponentName
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class CompositionLocalProvidersTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val serverUrl = MutableStateFlow<String?>(null)
    private val serverUseCase = mockk<ServerUseCase> {
        every { getServerUrl() } answers { serverUrl.value }
        every { observeMaybeServerUrl() } returns serverUrl
    }
    private val settingsUIUseCase = mockk<SettingsUIUseCase>(relaxed = true)
    private val underTest = CompositionLocalProviders(
        navigator = mockk(),
        exoplayerProvider = mockk(),
        settingsUIUseCase = settingsUIUseCase,
        fullImageLoader = mockk(),
        thumbnailImageLoader = mockk(),
        thumbnailImageWithNetworkCacheSupportLoader = mockk(),
        serverUseCase = serverUseCase,
        mapViewFactoryProviders = emptySet(),
        mapViewStateFactories = emptySet(),
    )

    @Before
    fun registerTheHostActivity() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        shadowOf(application.packageManager)
            .addActivityIfNotPresent(ComponentName(application, ComponentActivity::class.java))
    }

    @Test
    fun `provides the server url that is set after the first composition`() {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.setContent {
                    underTest.Provide {
                        Text(LocalServerUrl.current ?: "no server")
                    }
                }
            }

            serverUrl.value = "https://photos.example.com"

            composeRule.onNodeWithText("https://photos.example.com").assertExists()
        }
    }

    @Test
    fun `provides the available map provider from the first composition`() {
        every { settingsUIUseCase.getMapProvider() } returns MapProvider.Google
        every { settingsUIUseCase.observeMapProvider() } returns emptyFlow()
        val provided = mutableListOf<MapProvider>()

        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.setContent {
                    underTest.Provide {
                        provided += LocalMapProvider.current
                    }
                }
            }
            composeRule.waitForIdle()

            assertEquals(listOf(MapProvider.Google), provided.distinct())
        }
    }
}
