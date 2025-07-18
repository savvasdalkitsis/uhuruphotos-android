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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SearchState(
    val query: String = "",
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val clusterStates: ImmutableList<ClusterState> = persistentListOf(),
    val searchDisplay: CollageDisplayState = PredefinedCollageDisplayState.default,
) : Parcelable