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
package com.savvasdalkitsis.uhuruphotos.library.seam

import com.savvasdalkitsis.uhuruphotos.api.autoalbum.navigation.AutoAlbumNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryEffect.ErrorLoadingAutoAlbums
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryEffect.NavigateToAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import javax.inject.Inject

class LibraryEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<LibraryEffect> {

    override suspend fun handleEffect(effect: LibraryEffect) = when (effect) {
        ErrorLoadingAutoAlbums -> toaster.show(R.string.error_loading_auto_albums)
        is NavigateToAutoAlbum -> navigator
            .navigateTo(AutoAlbumNavigationTarget.name(effect.album.id))
    }
}