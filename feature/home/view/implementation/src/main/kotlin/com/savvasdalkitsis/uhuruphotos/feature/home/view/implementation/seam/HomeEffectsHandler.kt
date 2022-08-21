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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.HomeEffect.LoadFeed
import com.savvasdalkitsis.uhuruphotos.feature.server.view.api.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import javax.inject.Inject

internal class HomeEffectsHandler @Inject constructor(
    private val navigator: Navigator,
) : EffectHandler<HomeEffect> {

    override suspend fun handleEffect(effect: HomeEffect) {
        with(navigator) {
            when (effect) {
                LaunchAuthentication -> navigateTo(ServerNavigationTarget.name())
                LoadFeed -> {
                    navigateBack()
                    navigateTo(FeedNavigationTarget.name)
                }
            }
        }
    }
}