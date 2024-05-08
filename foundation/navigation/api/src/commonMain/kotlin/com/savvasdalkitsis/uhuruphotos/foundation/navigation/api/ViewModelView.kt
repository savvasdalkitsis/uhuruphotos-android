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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.hoc081098.kmp.viewmodel.CreationExtras
import com.hoc081098.kmp.viewmodel.ViewModelFactory
import com.hoc081098.kmp.viewmodel.compose.kmpViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
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
    val viewModel: VM = kmpViewModel(
        factory = object : ViewModelFactory<VM> {
            override val viewModelClass: KClass<VM> = viewModelClass
            override fun create(extras: CreationExtras): VM =
                viewModelClass.java.getDeclaredConstructor().newInstance()
        },
    )

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