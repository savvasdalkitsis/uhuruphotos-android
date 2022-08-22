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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class SelectionList @Inject constructor() {

    private var selectedIds = MutableStateFlow(emptySet<String>())

    val ids: Flow<Set<String>> = selectedIds

    suspend fun deselect(id: MediaId<*>) {
        selectedIds.emit(selectedIds.value - id.value.toString())
    }

    suspend fun select(id: MediaId<*>) {
        selectedIds.emit(selectedIds.value + id.value.toString())
    }

    suspend fun clear() {
        selectedIds.emit(emptySet())
    }
}