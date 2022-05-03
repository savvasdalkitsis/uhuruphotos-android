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
package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import com.savvasdalkitsis.uhuruphotos.home.module.HomeModule
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LaunchAuthentication
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect.LoadFeed
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import javax.inject.Inject

class HomeEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    @HomeModule.HomeNavigationTargetFeed private val feedNavigationTargetName: String,
) : (HomeEffect) -> Unit {

    override fun invoke(
        effect: HomeEffect,
    ) {
        with(controllersProvider.navController!!) {
            when (effect) {
                LaunchAuthentication -> navigate(ServerNavigationTarget.name())
                LoadFeed -> {
                    popBackStack()
                    navigate(feedNavigationTargetName)
                }
            }
        }
    }
}