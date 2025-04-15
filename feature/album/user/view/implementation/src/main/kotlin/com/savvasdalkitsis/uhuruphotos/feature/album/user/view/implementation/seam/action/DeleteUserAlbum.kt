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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.action

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.UserAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam.UserAlbumMutation
import com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.ui.state.UserAlbumState
import kotlinx.coroutines.flow.flow
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.error_deleting_album

data object DeleteUserAlbum : UserAlbumAction() {
    override fun UserAlbumActionsContext.handle(state: UserAlbumState) = flow<UserAlbumMutation> {
        userAlbumUseCase.deleteUserAlbum(state.albumId)
            .onSuccess { navigator.navigateUp() }
            .onFailure { toaster.show(string.error_deleting_album) }
    }
}
