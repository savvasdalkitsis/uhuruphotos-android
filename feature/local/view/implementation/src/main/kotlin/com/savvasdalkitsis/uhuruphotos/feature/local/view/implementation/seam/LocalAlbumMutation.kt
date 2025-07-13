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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed class LocalAlbumMutation(
    mutation: Mutation<LocalAlbumState>
) : Mutation<LocalAlbumState> by mutation {

    data class AlbumIdLoaded(val albumId: Int) : LocalAlbumMutation({
        it.copy(albumId = albumId)
    })

    data object PermissionsGranted : LocalAlbumMutation({
        it.copy(deniedPermissions = persistentListOf())
    })

    data class AskForPermissions(val deniedPermissions: List<String>) : LocalAlbumMutation({
        it.copy(deniedPermissions = deniedPermissions.toImmutableList())
    })

    data class DisplayContributingToPortfolio(val contributingToPortfolio: Boolean) : LocalAlbumMutation({
        it.copy(contributingToPortfolio = contributingToPortfolio)
    })
}