/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.app.navigation.AppNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalSystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.window.LocalWindowSize
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : FragmentActivity() {

    @Inject lateinit var navigator: AppNavigator
    @Inject lateinit var currentActivityHolder: CurrentActivityHolder
    @Inject lateinit var settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.enabled = true
        super.onCreate(savedInstanceState)
        actionBar?.let {
            // Attempt to initialize activity internals on devices that might be doing something
            // odd causing an NPE when using setContent further down (suggested by ASG group)
        }

        currentActivityHolder.onCreated(this)
        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        } catch (_: Exception) { /* safe to ignore */ }
        setContent {
            LaunchedEffect(Unit) {
                Log.enabled = settingsUseCase.getLoggingEnabled()
            }
            val systemUiController = rememberSystemUiController()
            val windowSizeClass = calculateWindowSizeClass(this)
            CompositionLocalProvider(
                LocalSystemUiController provides systemUiController,
                LocalWindowSize provides windowSizeClass,
            ) {
                navigator.NavigationTargets()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentActivityHolder.onDestroy()
    }
}