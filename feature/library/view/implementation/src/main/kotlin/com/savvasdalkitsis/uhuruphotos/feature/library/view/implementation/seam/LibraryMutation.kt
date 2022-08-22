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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMedia
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.MediaGridState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class LibraryMutation(
    mutation: Mutation<LibraryState>,
) : Mutation<LibraryState> by mutation {

    data class DisplayAutoAlbums(val cover: MediaGridState) : LibraryMutation({
        it.copy(autoAlbums = cover)
    })

    data class DisplayUserAlbums(val cover: MediaGridState) : LibraryMutation({
        it.copy(userAlbums = cover)
    })

    data class Loading(val loading: Boolean) : LibraryMutation({
        it.copy(loading = loading)
    })

    data class DisplayFavouritePhotos(val cover: MediaGridState) : LibraryMutation({
        it.copy(favouritePhotos = cover)
    })

    data class DisplayLocalAlbums(val localMedia: LibraryLocalMedia) : LibraryMutation({
        it.copy(localMedia = localMedia)
    })
}
