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
package com.savvasdalkitsis.uhuruphotos.autoalbum.viewmodel

import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumEffect
import com.savvasdalkitsis.uhuruphotos.autoalbum.mvflow.AutoAlbumEffect.*
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class AutoAlbumEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
) : EffectHandler<AutoAlbumEffect> {

    override suspend fun invoke(effect: AutoAlbumEffect) {
        when (effect) {
            is OpenPhotoDetails -> navigate(
                PhotoNavigationTarget.name(
                    effect.id,
                    effect.center,
                    effect.scale,
                    effect.video,
                )
            )
            NavigateBack -> controllersProvider.navController!!.popBackStack()
            is NavigateToPerson -> navigate(
                PersonNavigationTarget.name(effect.personId)
            )
        }
    }

    private fun navigate(name: String) {
        controllersProvider.navController!!.navigate(name)
    }
}