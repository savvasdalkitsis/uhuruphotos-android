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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.content.Intent
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRouteSerializer
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject internal constructor(
    private val intentLauncher: IntentLauncher,
    private val navigationRouteSerializer: NavigationRouteSerializer,
) : Navigator {
    override lateinit var navController: NavHostController

    override fun navigateTo(intent: Intent) {
        intentLauncher.launch(intent)
    }

    override fun navigateTo(intent: Intent, fallbackIntent: Intent) {
        intentLauncher.launch(intent, fallbackIntent)
    }

    override fun <R : NavigationRoute> navigateTo(route: R) {
        val path = navigationRouteSerializer.serialize(route)
        log { "Navigating to $path" }
        navController.navigate(path)
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun clearBackStack() {
        navController.clearBackStack(
            navigationRouteSerializer.createRouteTemplateFor(HomeNavigationRoute::class)
        )
    }
}