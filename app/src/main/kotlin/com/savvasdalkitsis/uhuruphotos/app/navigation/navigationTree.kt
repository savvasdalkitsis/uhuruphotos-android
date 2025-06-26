/*
Copyright 2023 Savvas Dalkitsis

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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import com.bugsnag.android.performance.BugsnagPerformance.startViewLoadSpan
import com.bugsnag.android.performance.ViewType.COMPOSE
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetRegistry

@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedTransitionScope.navigationTree(
    key: NavigationRoute,
): NavEntry<NavigationRoute> = NavEntry(key) {
    val klass = key::class
    val screenName = klass.simpleName ?: key.toString()
    val viewSpan = remember(screenName) {
        startViewLoadSpan(COMPOSE, screenName)
    }
    with(NavigationTargetRegistry.registry[klass]!!) {
        NavigationRootView(key)
    }
    LaunchedEffect(screenName) {
        viewSpan.end()
    }
}