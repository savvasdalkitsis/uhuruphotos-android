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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class NavigationViewModel<S : Any, A : Any, R : Any>(
    actionHandler: ActionHandler<S, A>,
    initialState: S,
) : ViewModel(), HasActionableState<S, A> {

    private val seam = Seam(
        actionHandler,
        initialState,
        viewModelScope,
    )

    init {
        viewModelScope.launch {
            state.collect {
                log("Seam") { "State updated to: $it" }
            }
        }
    }

    override val state: StateFlow<S>
        get() = seam.state

    override fun action(action: A) {
        seam.action(action)
    }

    abstract fun onRouteSet(route: R)
}