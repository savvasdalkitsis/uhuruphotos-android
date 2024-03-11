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
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

interface NavigationTargetBuilder {

    @Composable
    fun <S : Any, A : Any, VM : NavigationViewModel<S, A, R>, R : NavigationRoute> ViewModelView(
        themeMode: StateFlow<ThemeMode>,
        route: R,
        viewModel: KClass<VM>,
        scoped: Boolean,
        content: @Composable (state: S, actions: (A) -> Unit) -> Unit,
    )
}
