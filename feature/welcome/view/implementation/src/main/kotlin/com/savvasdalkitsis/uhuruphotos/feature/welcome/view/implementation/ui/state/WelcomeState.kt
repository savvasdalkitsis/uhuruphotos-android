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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class WelcomeState(
    val isLoading: Boolean = true,
    val isSaveEnabled: Boolean = false,
    val localMediaSelected: Boolean = false,
    val cloudMediaSelected: Boolean = false,
    val missingPermissions: ImmutableList<String>? = null,
    val showLibrePhotosHelp: Boolean = false,
)