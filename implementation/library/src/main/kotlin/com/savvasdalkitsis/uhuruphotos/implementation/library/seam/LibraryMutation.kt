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
package com.savvasdalkitsis.uhuruphotos.implementation.library.seam

import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoGrid
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState

sealed class LibraryMutation(
    mutation: Mutation<LibraryState>,
) : Mutation<LibraryState> by mutation {

    data class DisplayAutoAlbums(val cover: PhotoGrid) : LibraryMutation({
        it.copy(autoAlbums = cover)
    })

    data class DisplayUserAlbums(val cover: PhotoGrid) : LibraryMutation({
        it.copy(userAlbums = cover)
    })

    data class Loading(val loading: Boolean) : LibraryMutation({
        it.copy(loading = loading)
    })

    data class DisplayFavouritePhotos(val cover: PhotoGrid) : LibraryMutation({
        it.copy(favouritePhotos = cover)
    })
}
