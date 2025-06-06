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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import dev.burnoo.compose.rememberpreference.rememberBooleanPreference
import org.jetbrains.compose.resources.StringResource

data class CollapsibleGroupState(
    val title: StringResource,
    val collapsed: MutableState<Boolean>,
) {

    val isCollapsed get() = collapsed.value
    fun toggleCollapsed() {
        collapsed.value = !collapsed.value
    }
    fun collapse() {
        collapsed.value = false
    }
    fun expand() {
        collapsed.value = true
    }
}

@Composable
fun rememberCollapsibleGroupState(
    title: StringResource,
    uniqueKey: String,
    initiallyCollapsed: Boolean = false
): CollapsibleGroupState {
    val state = rememberBooleanPreference(keyName = "collapsible_group:$uniqueKey", initiallyCollapsed, initiallyCollapsed)
    return CollapsibleGroupState(title, state)
}