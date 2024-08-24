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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.toUserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsMutation.DisplayAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsMutation.SetFilter
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.andThen
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data object Load : UserAlbumsAction() {
    context(UserAlbumsActionsContext) override fun handle(
        state: UserAlbumsState
    ) = merge(
        combine(
        userAlbumsUseCase.observeUserAlbums()
            .mapNotNull { albums ->
                albums.map { it.toUserAlbumState() }
            },
            filterText,
        ) { albums, filter ->
            DisplayAlbums(albums.map {
                it.copy(
                    visible = when {
                        filter.isBlank() -> true
                        else -> (it.title as? Title.Text)?.title?.contains(filter, ignoreCase = true) ?: true
                    }
                )
            }) andThen SetFilter(filter)
        },
        loading
            .map(UserAlbumsMutation::Loading)
    ).safelyOnStartIgnoring {
        if (userAlbumsUseCase.getUserAlbums().isEmpty()) {
            refreshAlbums()
        }
    }

}
