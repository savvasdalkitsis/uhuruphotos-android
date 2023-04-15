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

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.api.navigation.AutoAlbumNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.ErrorLoadingAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsEffect.NavigateToAutoAlbum
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import javax.inject.Inject

class AutoAlbumsEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
): EffectHandler<AutoAlbumsEffect> {

    override suspend fun handleEffect(effect: AutoAlbumsEffect) {
        when (effect) {
            ErrorLoadingAlbums -> toaster.show(string.error_loading_auto_albums)
            NavigateBack -> navigator.navigateBack()
            is NavigateToAutoAlbum ->
                navigator.navigateTo(AutoAlbumNavigationRoute(effect.album.id))
        }
    }
}