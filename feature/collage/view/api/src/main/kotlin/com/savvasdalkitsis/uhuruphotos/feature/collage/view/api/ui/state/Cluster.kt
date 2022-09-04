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

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel

data class Cluster(
    val id: String,
    val cels: List<CelState>,
    val displayTitle: String,
    val unformattedDate: String? = null,
    val location: String?,
)

val previewClusterEmpty = Cluster(
    "id",
    emptyList(),
    displayTitle = "01 January 2022",
    unformattedDate = "2022-01-01",
    location = "London, UK",
)

fun MediaCollection.toCluster() = Cluster(
    id = id,
    cels = mediaItems.map { it.toCel() },
    displayTitle = displayTitle,
    unformattedDate = unformattedDate,
    location = location,
)