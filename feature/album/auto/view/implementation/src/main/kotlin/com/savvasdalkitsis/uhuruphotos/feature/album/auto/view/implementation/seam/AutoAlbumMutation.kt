/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class AutoAlbumMutation(
    mutation: Mutation<AutoAlbumState>,
) : Mutation<AutoAlbumState> by mutation {

    data class ShowDeleteConfirmationDialog(val show: Boolean) : AutoAlbumMutation({
        it.copy(showDeleteConfirmationDialog = show)
    })

    data class SetAlbumId(val id: Int) : AutoAlbumMutation({
        it.copy(albumId = id)
    })
}