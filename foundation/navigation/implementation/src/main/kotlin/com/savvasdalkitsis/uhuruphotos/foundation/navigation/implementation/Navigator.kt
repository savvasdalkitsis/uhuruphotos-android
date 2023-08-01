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

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class Navigator @Inject internal constructor(
    @ApplicationContext private val context: Context,
    private val intentLauncher: IntentLauncher,
) : Navigator {

    private val routeKey = "route"

    override lateinit var backStack: BackStack<NavigationRoute>

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

    override fun clearBackStack() {
        newRoot(HomeNavigationRoute)
    }

    override fun <R : NavigationRoute> newRoot(route: R) {
        onMain {
            backStack.newRoot(route)
        }
    }

    override fun maybeHandleDeeplink(intent: Intent?) {
        if (SDK_INT >= TIRAMISU) {
            intent?.extras?.getParcelable(routeKey, NavigationRoute::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.extras?.getParcelable(routeKey)
        }?.let(::navigateTo)
    }

    override fun <R : NavigationRoute> deepLinkTo(route: R) {
        navigateTo(Intent(Intent.ACTION_VIEW).apply {
            setClassName(context, "com.savvasdalkitsis.uhuruphotos.app.AppActivity")
            putExtra(routeKey, route)
        })
    }

}