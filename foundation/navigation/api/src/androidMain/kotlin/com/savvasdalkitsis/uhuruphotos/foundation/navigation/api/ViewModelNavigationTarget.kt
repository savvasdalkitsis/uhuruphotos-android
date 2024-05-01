/*
Copyright 2024 Savvas Dalkitsis

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

import android.app.Application
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.auto.AutoInitialize
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.AppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ViewModelNavigationTarget<S : Any, A : Any, VM : NavigationViewModel<S, A, R>, R: NavigationRoute>(
    private val viewModelClass: KClass<VM>,
    private val route: KClass<R>,
    private val viewModelScopedToComposable: Boolean = true,
    private val theme: @Composable () -> ThemeMode = { LocalThemeMode.current },
    private val view: @Composable (S, (A) -> Unit) -> Unit,
) : NavigationTarget<R>, ApplicationCreated {

    override fun onAppCreated(app: Application) {
        NavigationTargetRegistry.register(route, this as NavigationTarget<NavigationRoute>)
    }

    @Composable
    override fun NavigationRootView(route: R) {
        ViewModelView(route, viewModelClass, viewModelScopedToComposable) { state, actions ->
            AppTheme(theme = theme()) {
                view(state, actions)
            }
        }
    }
}