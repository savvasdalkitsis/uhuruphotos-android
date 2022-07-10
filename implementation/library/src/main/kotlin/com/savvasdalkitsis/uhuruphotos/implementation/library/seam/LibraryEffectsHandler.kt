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

import com.savvasdalkitsis.uhuruphotos.api.autoalbums.navigation.AutoAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.favourites.navigation.FavouritesNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.hidden.navigation.HiddenPhotosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.favourites.navigation.TrashNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.api.useralbums.navigation.UserAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryEffect.*
import javax.inject.Inject

class LibraryEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<LibraryEffect> {

    override suspend fun handleEffect(effect: LibraryEffect) = when (effect) {
        ErrorLoadingAlbums -> toaster.show(R.string.error_loading_albums)
        NavigateToAutoAlbums -> navigator
            .navigateTo(AutoAlbumsNavigationTarget.name)
        NavigateToUserAlbums -> navigator
            .navigateTo(UserAlbumsNavigationTarget.name)
        NavigateToFavourites -> navigator
            .navigateTo(FavouritesNavigationTarget.name)
        NavigateToHidden -> navigator
            .navigateTo(HiddenPhotosNavigationTarget.name)
        NavigateToTrash -> navigator
            .navigateTo(TrashNavigationTarget.name)
    }
}