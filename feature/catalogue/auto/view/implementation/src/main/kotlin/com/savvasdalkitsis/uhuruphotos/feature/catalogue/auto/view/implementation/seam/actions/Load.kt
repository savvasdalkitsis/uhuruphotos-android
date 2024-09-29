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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation.DisplayAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsMutation.SetFilter
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.andThen
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data object Load : AutoAlbumsAction() {
    override fun AutoAlbumsActionsContext.handle(
        state: AutoAlbumsState
    ) = merge(
        combine(
            autoAlbumsUseCase.observeAutoAlbums(),
            filterText,
        ) { albums, filter ->
            DisplayAlbums(albums.map {
                it.copy(
                    visible = when {
                        filter.isBlank() -> true
                        else -> it.title.contains(filter, ignoreCase = true)
                    }
                )
            }) andThen SetFilter(filter)
        },
        loading
            .map(AutoAlbumsMutation::Loading)
    ).safelyOnStartIgnoring {
        if (autoAlbumsUseCase.getAutoAlbums().isEmpty()) {
            refreshAlbums()
        }
    }
}
