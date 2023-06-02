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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRouteSerializer
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.AppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.reflect.KClass

@AutoBind
class NavigationTargetBuilder @Inject constructor(
    private val serializer: NavigationRouteSerializer,
) : NavigationTargetBuilder {

    override fun <S : Any, A : Any, VM : NavigationViewModel<S, *, A, R>, R : NavigationRoute> NavGraphBuilder.navigationTarget(
        themeMode: StateFlow<ThemeMode>,
        route: KClass<R>,
        viewModel: KClass<VM>,
        content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
    ) {
        val routePath = serializer.createRouteTemplateFor(route)
        composable(
            routePath,
        ) { navBackStackEntry ->
            val model: VM = hiltViewModel(viewModel)

            val keyboard = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                keyboard?.hide()
                model.setRoute(serializer.deserialize(
                    route,
                    navBackStackEntry.arguments
                ))
            }
            val scope = rememberCoroutineScope()
            val action: (A) -> Unit = {
                scope.launch {
                    model.action(it)
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
                content(state, action)
            }
        }
    }

    @Composable
    private fun <VM : ViewModel> hiltViewModel(
        klass: KClass<VM>,
        viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        key: String? = null
    ): VM {
        val factory = createHiltViewModelFactory(viewModelStoreOwner)
        return viewModel(klass, viewModelStoreOwner, key, factory = factory)
    }

    @Composable
    private fun createHiltViewModelFactory(
        viewModelStoreOwner: ViewModelStoreOwner
    ): ViewModelProvider.Factory? = if (viewModelStoreOwner is NavBackStackEntry) {
        HiltViewModelFactory(
            context = LocalContext.current,
            navBackStackEntry = viewModelStoreOwner
        )
    } else {
        // Use the default factory provided by the ViewModelStoreOwner
        // and assume it is an @AndroidEntryPoint annotated fragment or activity
        null
    }

    @Composable
    private fun <VM : ViewModel> viewModel(
        klass: KClass<VM>,
        viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
        key: String? = null,
        factory: ViewModelProvider.Factory? = null,
        extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
            viewModelStoreOwner.defaultViewModelCreationExtras
        } else {
            CreationExtras.Empty
        }
    ): VM = androidx.lifecycle.viewmodel.compose.viewModel(
        klass.java,
        viewModelStoreOwner,
        key,
        factory,
        extras
    )
}
