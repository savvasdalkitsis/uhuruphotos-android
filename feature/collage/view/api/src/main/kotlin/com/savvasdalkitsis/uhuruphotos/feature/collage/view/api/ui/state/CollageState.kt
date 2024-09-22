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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class CollageState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val clusterStates: ImmutableList<ClusterState> = persistentListOf(),
    val collageDisplayState: CollageDisplayState = PredefinedCollageDisplayState.default,
) {
    val hasMedia get() = clusterStates.sumOf { it.cels.size } > 0

    override fun toString(): String {
        return "CollageState(isLoading=$isLoading, isEmpty=$isEmpty, clustersSize=${clusterStates.size}, collageDisplay=$collageDisplayState)"
    }
}