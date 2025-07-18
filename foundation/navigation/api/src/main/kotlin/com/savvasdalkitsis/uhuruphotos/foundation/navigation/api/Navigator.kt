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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import android.content.Intent
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.reflect.KClass

interface Navigator {
    val backStack: SnapshotStateList<NavigationRoute>

    fun navigateToWeb(webUrl: String)
    fun navigateTo(intent: Intent)
    fun navigateTo(intent: Intent, fallbackIntent: Intent)
    fun <R : NavigationRoute> navigateTo(route: R)
    fun <R : NavigationRoute> newRoot(route: R)
    fun navigateBack()
    fun navigateUp()
    fun clearBackStack()
    fun <R : NavigationRoute> singleTop(route: R, routeType: KClass<R>)
}

inline fun <reified R : NavigationRoute> Navigator.singleTop(route: R) {
    singleTop(route, R::class)
}