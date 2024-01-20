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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.os.Bundle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.AppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.sebaslogen.resaca.ScopedViewModelContainer
import com.sebaslogen.resaca.generateKeysAndObserveLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.StateFlow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.reflect.KClass

@AutoBind
class NavigationTargetBuilder @Inject constructor() : NavigationTargetBuilder {

    @Composable
    override fun <S : Any, A : Any, VM : NavigationViewModel<S, A, R>, R : NavigationRoute> ViewModelView(
        themeMode: StateFlow<ThemeMode>,
        route: R,
        viewModel: KClass<VM>,
        content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
    ) {
        val model: VM = hiltViewModelScoped(route.toString(), viewModel)

        val keyboard = LocalSoftwareKeyboardController.current
        DisposableEffect(route) {
            log { "Navigated to route: $route" }
            keyboard?.hide()
            model.onRouteSet(route)
            onDispose {
                model.viewModelScope.coroutineContext[Job]?.cancelChildren()
            }
        }
        val state by model.state.collectAsState()
        val theme by themeMode.collectAsState()
        val dark = when (theme) {
            ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
            ThemeMode.DARK_MODE -> true
            ThemeMode.LIGHT_MODE -> false
        }
        AppTheme(dark) {
            content(state, model::action)
        }
    }

    @Composable
    private fun <T : ViewModel> hiltViewModelScoped(key: Any? = null, clazz: KClass<T>, defaultArguments: Bundle = Bundle.EMPTY): T {
        val (scopedViewModelContainer: ScopedViewModelContainer, positionalMemoizationKey: ScopedViewModelContainer.InternalKey, externalKey: ScopedViewModelContainer.ExternalKey) =
            generateKeysAndObserveLifecycle(key = key)

        val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        }

        // The object will be built the first time and retrieved in next calls or recompositions
        return scopedViewModelContainer.getOrBuildViewModel(
            modelClass = clazz.java,
            positionalMemoizationKey = positionalMemoizationKey,
            externalKey = externalKey,
            factory = com.sebaslogen.resaca.hilt.createHiltViewModelFactory(viewModelStoreOwner),
            viewModelStoreOwner = viewModelStoreOwner,
            defaultArguments = defaultArguments
        )
    }
}
