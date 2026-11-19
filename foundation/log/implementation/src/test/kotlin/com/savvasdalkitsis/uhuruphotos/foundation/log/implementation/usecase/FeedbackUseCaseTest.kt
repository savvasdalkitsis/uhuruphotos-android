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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.usecase

import android.app.Application
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode.DARK_MODE
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import org.jetbrains.compose.resources.getString
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FeedbackUseCaseTest {

    private val app: Application = RuntimeEnvironment.getApplication()
    private val settingsUI = mockk<SettingsUIUseCase> { every { getThemeMode() } returns DARK_MODE }
    private val underTest = FeedbackUseCase(app, mockk(), settingsUI)

    @Before
    fun interceptLogViewer() {
        mockkStatic(VIEWER, RESOURCES)
        every { L.showLog(any(), any(), any(), any(), any()) } returns Unit
        coEvery { getString(any()) } returns "Logs"
    }

    @After
    fun restore() {
        unmockkStatic(VIEWER, RESOURCES)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_UNSPECIFIED)
    }

    @Test
    fun `applies the theme to open screens on the main thread when showing logs`() {
        Robolectric.buildActivity(ThemedActivity::class.java).setup()

        val showing = CoroutineScope(Dispatchers.Default).async { runCatching { underTest.showLogs() } }
        val deadline = System.currentTimeMillis() + 5000
        while (!showing.isCompleted && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
        }

        assertNull(showing.getCompleted().exceptionOrNull())
        assertEquals(MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    private companion object {
        const val VIEWER = "com.michaelflisar.lumberjack.extensions.viewer.ExtensionsKt"
        const val RESOURCES = "org.jetbrains.compose.resources.StringResourcesKt"
    }
}

class ThemedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        super.onCreate(savedInstanceState)
    }
}
