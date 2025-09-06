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
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import dagger.hilt.android.qualifiers.ApplicationContext
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@AutoBind
@Singleton
class Navigator @Inject internal constructor(
    @ApplicationContext private val context: Context,
    private val intentLauncher: IntentLauncher,
) : Navigator {

    private val _backStack = mutableStateListOf<NavigationRoute>(HomeNavigationRoute)
    override val backStack: SnapshotStateList<NavigationRoute> = _backStack

    override fun navigateToWeb(webUrl: String) = navigateTo(
        Intent(Intent.ACTION_VIEW, webUrl.toUri())
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
        mutateBackStack {
            add(route)
        }
    }

    override fun navigateBack() {
        mutateBackStack {
            removeLastOrNull()
        }
    }

    override fun navigateUp() {
        if (_backStack.size > 1) {
            navigateBack()
        } else {
            clearBackStack()
        }
    }

    override fun navigateToAppSystemSettings() {
        navigateTo(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.Builder()
            .scheme("package")
            .opaquePart(context.packageName)
            .build()
        ))
    }

    override fun clearBackStack() {
        newRoot(HomeNavigationRoute)
    }

    override fun <R : NavigationRoute> newRoot(route: R) {
        mutateBackStack {
            clear()
            add(route)
        }
    }

    override fun <R : NavigationRoute> singleTop(route: R, routeType: KClass<R>) {
        mutateBackStack {
            if (any { it::class == routeType }) {
                while (last()::class != routeType) {
                    removeLastOrNull()
                }
                removeLastOrNull()
            }
            add(route)
        }
    }

    private fun mutateBackStack(action: SnapshotStateList<NavigationRoute>.() -> Unit) {
        onMain {
            _backStack.action()
            log { "Back stack is now: ${_backStack.joinToString(", ")}" }
        }
    }
}