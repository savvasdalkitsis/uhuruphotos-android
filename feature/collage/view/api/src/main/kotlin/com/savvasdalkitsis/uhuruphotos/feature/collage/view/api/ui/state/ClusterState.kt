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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.NewCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ClusterState(
    val id: String,
    val cels: ImmutableList<CelState> = persistentListOf(),
    val displayTitle: String = "",
    val unformattedDate: String? = null,
    val location: String? = null,
    val showRefreshIcon: Boolean = false,
) : Parcelable {
    val hasAnyCelsWithRemoteMedia = cels.any { it.mediaItem.id.preferRemote is MediaIdModel.RemoteIdModel }
}

@Immutable
@Parcelize
data class NewClusterState(
    val cels: ImmutableList<NewCelState> = persistentListOf(),
    val displayTitle: String = "",
    val location: String? = null,
) : Parcelable

val previewClusterStateEmpty = ClusterState(
    "id",
    persistentListOf(),
    displayTitle = "01 January 2022",
    unformattedDate = "2022-01-01",
    location = "London, UK",
    showRefreshIcon = true,
)

fun MediaCollectionModel.toCluster() = ClusterState(
    id = id,
    cels = mediaItems.map { it.toCel() }.toImmutableList(),
    displayTitle = displayTitle,
    unformattedDate = unformattedDate,
    location = location,
)