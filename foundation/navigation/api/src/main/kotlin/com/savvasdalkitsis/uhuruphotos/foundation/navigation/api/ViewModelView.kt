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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.sebaslogen.resaca.ScopedViewModelContainer
import com.sebaslogen.resaca.generateKeysAndObserveLifecycle
import com.sebaslogen.resaca.hilt.createHiltViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlin.reflect.KClass

@Composable
fun <S : Any, A : Any, VM : NavigationViewModel<S, A, R>, R : NavigationRoute> ViewModelView(
    route: R,
    viewModelClass: KClass<VM>,
    viewModelScopedToComposable: Boolean,
    content: @Composable (state: S, action: (A) -> Unit) -> Unit,
) {
    val viewModel: VM = if (viewModelScopedToComposable) {
        hiltViewModelScoped(route.toString(), viewModelClass)
    } else {
        hiltViewModel(route.toString(), viewModelClass)
    }

    val keyboard = LocalSoftwareKeyboardController.current
    DisposableEffect(route) {
        log { "Navigated to route: $route" }
        keyboard?.hide()
        viewModel.onRouteSet(route)
        onDispose {
            viewModel.viewModelScope.coroutineContext[Job]?.cancelChildren()
        }
    }
    val state by viewModel.state.collectAsState()
    content(state, viewModel::action)
}

@Composable
private fun <VM : ViewModel> hiltViewModel(
    key: String? = null,
    clazz: KClass<VM>,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
): VM {
    val factory = createHiltViewModelFactory(viewModelStoreOwner)
    return viewModel(key, factory, clazz)
}

@Composable
private fun <VM : ViewModel> viewModel(
    key: String? = null,
    factory: ViewModelProvider.Factory? = null,
    clazz: KClass<VM>,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
): VM = androidx.lifecycle.viewmodel.compose.viewModel(
    clazz.java,
    viewModelStoreOwner,
    key,
    factory,
    extras
)

@Composable
private fun <T : ViewModel> hiltViewModelScoped(key: Any? = null, clazz: KClass<T>, defaultArguments: Bundle = Bundle.EMPTY): T {
    val (scopedViewModelContainer: ScopedViewModelContainer, positionalMemoizationKey: ScopedViewModelContainer.InternalKey, externalKey: ScopedViewModelContainer.ExternalKey) =
        generateKeysAndObserveLifecycle(key = key)

    val viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    // The object will be built the first time and retrieved in next calls or recompositions
    return scopedViewModelContainer.getOrBuildViewModel(
        modelClass = clazz,
        positionalMemoizationKey = positionalMemoizationKey,
        externalKey = externalKey,
        factory = createHiltViewModelFactory(viewModelStoreOwner),
        viewModelStoreOwner = viewModelStoreOwner,
        defaultArguments = defaultArguments
    )
}