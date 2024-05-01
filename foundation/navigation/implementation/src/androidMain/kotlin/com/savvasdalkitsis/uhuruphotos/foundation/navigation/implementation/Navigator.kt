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
import android.net.Uri
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator

class Navigator(
    private val intentLauncher: IntentLauncher,
) : Navigator {

    override lateinit var backStack: BackStack<NavigationRoute>

    override fun navigateToWeb(webUrl: String) = navigateTo(
        Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
    )

    override fun navigateTo(intent: Intent) {
        onMain {
            intentLauncher.launch(intent)
        }
    }

    override fun navigateTo(intent: Intent, fallbackIntent: Intent) {
        onMain {
            intentLauncher.launch(intent, fallbackIntent)
        }
    }

    override fun <R : NavigationRoute> navigateTo(route: R) {
        log { "Navigating to $route" }
        onMain {
            backStack.push(route)
        }
    }

    override fun navigateBack() {
        onMain {
            backStack.pop()
        }
    }

    override fun navigateUp() {
        onMain {
            if (backStack.elements.value.all.size > 1) {
                navigateBack()
            } else {
                clearBackStack()
            }
        }
    }

    override fun clearBackStack() {
        newRoot(HomeNavigationRoute)
    }

    override fun <R : NavigationRoute> newRoot(route: R) {
        onMain {
            backStack.newRoot(route)
        }
    }
}