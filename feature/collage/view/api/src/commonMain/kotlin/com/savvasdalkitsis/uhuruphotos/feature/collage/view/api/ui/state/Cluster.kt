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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Immutable
data class Cluster(
    val id: String,
    val cels: ImmutableList<CelState> = persistentListOf(),
    val displayTitle: String = "",
    val unformattedDate: String? = null,
    val location: String? = null,
    val showRefreshIcon: Boolean = false,
) {
    val hasAnyCelsWithRemoteMedia = cels.any { it.mediaItem.id.preferRemote is MediaId.Remote }
}

val previewClusterEmpty = Cluster(
    "id",
    persistentListOf(),
    displayTitle = "01 January 2022",
    unformattedDate = "2022-01-01",
    location = "London, UK",
    showRefreshIcon = true,
)

fun MediaCollection.toCluster() = Cluster(
    id = id,
    cels = mediaItems.map { it.toCel() }.toPersistentList(),
    displayTitle = displayTitle,
    unformattedDate = unformattedDate,
    location = location,
)