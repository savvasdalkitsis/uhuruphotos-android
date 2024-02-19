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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation.AskForPermissions
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation.DisplayContributingToPortfolio
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumMutation.PermissionsGranted
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data class Load(val albumId: Int) : LocalAlbumAction() {
    context(LocalAlbumActionsContext) override fun handle(
        state: LocalAlbumState
    ) = merge(
        flow { galleryId = albumId },
        localMediaUseCase.observePermissionsState()
            .flatMapLatest {
                when (it) {
                    is LocalPermissions.RequiresPermissions -> flowOf(AskForPermissions(it.deniedPermissions))
                    else -> flow<LocalAlbumMutation> {
                        emit(PermissionsGranted)
                        localMediaUseCase.refreshLocalMediaFolder(albumId)
                    }
                }
            },
        portfolioUseCase.observePublishedFolderIds()
            .mapNotNull {
                val primaryFolder = localMediaUseCase.getDefaultBucketId()
                if (primaryFolder != albumId) {
                    DisplayContributingToPortfolio(albumId in it)
                } else {
                    null
                }
            }
    )
}
