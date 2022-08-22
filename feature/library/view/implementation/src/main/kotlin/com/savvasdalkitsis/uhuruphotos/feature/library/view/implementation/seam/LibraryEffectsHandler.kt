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

import com.savvasdalkitsis.uhuruphotos.api.autoalbums.navigation.AutoAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.useralbums.navigation.UserAlbumsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.favourites.view.api.navigation.FavouritesNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.api.HiddenPhotosNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToAutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToFavourites
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToHidden
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToLocalBucket
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToTrash
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.LibraryEffect.NavigateToUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.local.view.api.navigation.LocalAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.api.navigation.TrashNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import javax.inject.Inject

class LibraryEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<LibraryEffect> {

    override suspend fun handleEffect(effect: LibraryEffect) = when (effect) {
        ErrorLoadingAlbums -> toaster.show(string.error_loading_albums)
        NavigateToAutoAlbums -> navigator
            .navigateTo(AutoAlbumsNavigationTarget.name)
        NavigateToUserAlbums -> navigator
            .navigateTo(UserAlbumsNavigationTarget.name)
        NavigateToFavourites -> navigator
            .navigateTo(FavouritesNavigationTarget.registrationName)
        NavigateToHidden -> navigator
            .navigateTo(HiddenPhotosNavigationTarget.registrationName)
        NavigateToTrash -> navigator
            .navigateTo(TrashNavigationTarget.registrationName)
        is NavigateToLocalBucket -> navigator
            .navigateTo(LocalAlbumNavigationTarget.name(effect.bucket.id))
    }
}