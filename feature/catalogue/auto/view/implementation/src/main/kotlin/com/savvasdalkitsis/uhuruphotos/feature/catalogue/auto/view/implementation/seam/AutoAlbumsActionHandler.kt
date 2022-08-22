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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.AutoAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.Load
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.NavigateToAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation.DisplayAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation.Loading
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class AutoAlbumsActionHandler @Inject constructor(
    private val autoAlbumsUseCase: AutoAlbumsUseCase,
): ActionHandler<AutoAlbumsState, AutoAlbumsEffect, AutoAlbumsAction, AutoAlbumsMutation> {

    private val loading = MutableSharedFlow<Boolean>()

    override fun handleAction(
        state: AutoAlbumsState,
        action: AutoAlbumsAction,
        effect: suspend (AutoAlbumsEffect) -> Unit
    ): Flow<AutoAlbumsMutation> = when (action) {
        Load -> merge(
            autoAlbumsUseCase.observeAutoAlbums()
                .map(::DisplayAlbums),
            loading
                .map(::Loading)
        ).safelyOnStartIgnoring {
            if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
                refreshAlbums(effect)
            }
        }
        is AutoAlbumSelected -> flow {
            effect(NavigateToAutoAlbum(action.album))
        }
        NavigateBack -> flow {
            effect(AutoAlbumsEffect.NavigateBack)
        }
        Refresh -> flow {
            refreshAlbums(effect)
        }
        is ChangeSorting -> flow {
            autoAlbumsUseCase.changeAutoAlbumsSorting(action.sorting)
        }
    }

    private suspend fun refreshAlbums(effect: suspend (AutoAlbumsEffect) -> Unit) {
        loading.emit(true)
        val result = autoAlbumsUseCase.refreshAutoAlbums()
        if (result.isFailure) {
            effect(ErrorLoadingAlbums)
        }
        // delaying to give ui time to receive the new albums before
        // dismissing the loading bar since no albums logic relies on that
        delay(500)
        loading.emit(false)
    }
}
