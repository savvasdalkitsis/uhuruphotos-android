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

import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryAutoAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryUserAlbum

sealed class LibraryMutation(
    mutation: Mutation<LibraryState>,
) : Mutation<LibraryState> by mutation {

    data class DisplayAutoAlbums(val albums: List<LibraryAutoAlbum>) : LibraryMutation({
        it.copy(autoAlbums = albums)
    })

    data class DisplayUserAlbums(val albums: List<LibraryUserAlbum>) : LibraryMutation({
        it.copy(userAlbums = albums)
    })

    data class ShowAutoAlbumSorting(val sorting: AlbumSorting) : LibraryMutation({
        it.copy(autoAlbumSorting = sorting)
    })

    data class ShowUserAlbumSorting(val sorting: AlbumSorting) : LibraryMutation({
        it.copy(userAlbumSorting = sorting)
    })

    data class AutoAlbumsLoading(val loading: Boolean) : LibraryMutation({
        it.copy(autoAlbumsLoading = loading)
    })

    data class UserAlbumsLoading(val loading: Boolean) : LibraryMutation({
        it.copy(userAlbumsLoading = loading)
    })
}
