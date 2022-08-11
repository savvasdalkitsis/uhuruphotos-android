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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam

import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.useralbums.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.Load
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.Refresh
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.UserAlbumSelected
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsEffect.NavigateToUserAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsMutation.DisplayAlbums
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsMutation.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class UserAlbumsActionHandler @Inject constructor(
    private val userAlbumsUseCase: UserAlbumsUseCase,
): ActionHandler<UserAlbumsState, UserAlbumsEffect, UserAlbumsAction, UserAlbumsMutation> {

    private val loading = MutableSharedFlow<Boolean>()

    override fun handleAction(
        state: UserAlbumsState,
        action: UserAlbumsAction,
        effect: suspend (UserAlbumsEffect) -> Unit
    ): Flow<UserAlbumsMutation> = when (action) {
        Load -> merge(
            userAlbumsUseCase.observeUserAlbums()
                .map(::DisplayAlbums),
            loading
                .map(::Loading)
        ).safelyOnStartIgnoring {
            if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
                refreshAlbums(effect)
            }
        }
        is UserAlbumSelected -> flow {
            effect(NavigateToUserAlbum(action.album))
        }
        NavigateBack -> flow {
            effect(UserAlbumsEffect.NavigateBack)
        }
        Refresh -> flow {
            refreshAlbums(effect)
        }
        is ChangeSorting -> flow {
            userAlbumsUseCase.changeUserAlbumsSorting(action.sorting)
        }
    }

    private suspend fun refreshAlbums(effect: suspend (UserAlbumsEffect) -> Unit) {
        loading.emit(true)
        val result = userAlbumsUseCase.refreshUserAlbums()
        if (result.isFailure) {
            effect(ErrorLoadingAlbums)
        }
        // delaying to give ui time to receive the new albums before
        // dismissing the loading bar since no albums logic relies on that
        delay(500)
        loading.emit(false)
    }
}
