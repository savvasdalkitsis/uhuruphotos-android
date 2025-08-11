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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.NewClusterState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class GalleryDetailsState(
    val title: Title = Title.Text(""),
    val clusterStates: ImmutableList<NewClusterState> = persistentListOf(),
    val people: ImmutableList<PersonState> = persistentListOf(),
)