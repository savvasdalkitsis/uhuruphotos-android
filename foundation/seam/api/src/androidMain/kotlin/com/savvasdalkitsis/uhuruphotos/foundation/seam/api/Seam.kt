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
package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Seam<S : Any, A : Any>(
    private val actionHandler: ActionHandler<S, A>,
    initialState: S,
    private val scope: CoroutineScope,
) : HasActionableState<S, A> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state

    override fun action(action: A) {
        scope.launch {
            log("Seam") { "Starting handling of action $action" }
            actionHandler.handleAction(_state.value, action,)
                .distinctUntilChanged()
                .flowOn(Dispatchers.Default)
                .cancellable()
                .collect { mutation ->
                    if (!scope.isActive)
                        currentCoroutineContext().cancel()
                    log("Seam") { "Received mutation $mutation due to action $action" }
                    _state.update { mutation.reduce(_state.value) }
                }
        }
    }
}