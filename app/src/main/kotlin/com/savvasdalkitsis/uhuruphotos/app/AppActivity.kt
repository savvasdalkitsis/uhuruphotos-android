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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.bugsnag.android.Bugsnag
import com.savvasdalkitsis.uhuruphotos.app.implementation.Initialization
import com.savvasdalkitsis.uhuruphotos.app.module.AppModule
import com.savvasdalkitsis.uhuruphotos.app.navigation.AppNavigator
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import kotlin.LazyThreadSafetyMode.NONE

class AppActivity : FragmentNodeActivity() {

    private val navigator: AppNavigator by lazy(NONE) {
        AppModule.appNavigator
    }
    private val settingsUseCase: SettingsUseCase by lazy(NONE) {
        SettingsModule.settingsUseCase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.enabled = true
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Bugsnag.start(this)
        Initialization.activityInitializer.onActivityCreated(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val logging by settingsUseCase.observeLoggingEnabled().collectAsState(initial = false)
            LaunchedEffect(logging) {
                Log.enabled = logging
            }
            val windowSizeClass = calculateWindowSizeClass()
            CompositionLocalProvider(
                LocalWindowSize provides windowSizeClass,
            ) {
                navigator.NavigationTargets(appyxIntegrationPoint)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Initialization.activityInitializer.onActivityDestroyed(this)
    }
}