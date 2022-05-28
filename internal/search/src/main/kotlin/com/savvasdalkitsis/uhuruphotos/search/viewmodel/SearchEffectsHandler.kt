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
package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.api.heatmap.navigation.HeatMapNavigationTarget
import com.savvasdalkitsis.uhuruphotos.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.people.api.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.model.PhotoSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.ErrorSearching
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.HideKeyboard
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.NavigateToAllPeople
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.NavigateToEditServer
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.NavigateToHeatMap
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.viewmodel.EffectHandler
import javax.inject.Inject

class SearchEffectsHandler @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val toaster: Toaster,
) : EffectHandler<SearchEffect> {

    override suspend fun invoke(
        effect: SearchEffect,
    ) = when (effect) {
        HideKeyboard -> controllersProvider.keyboardController!!.hide()
        ReloadApp -> {
            with(controllersProvider.navController!!) {
                backQueue.clear()
                navigate(HomeNavigationRoutes.home)
            }
        }
        NavigateToEditServer -> navigateTo(
            ServerNavigationTarget.name(auto = false)
        )
        NavigateToSettings -> navigateTo(SettingsNavigationTarget.name)
        is OpenPhotoDetails -> navigateTo(
            with(effect) {
                PhotoNavigationTarget.name(id, center, scale, isVideo, SearchResults(currentQuery))
            }
        )
        ErrorSearching -> toaster.show(R.string.error_searching)
        NavigateToAllPeople -> navigateTo(PeopleNavigationTarget.name)
        ErrorRefreshingPeople -> toaster.show(R.string.error_refreshing_people)
        is NavigateToPerson -> navigateTo(PersonNavigationTarget.name(effect.personId))
        NavigateToHeatMap -> navigateTo(HeatMapNavigationTarget.name)
    }

    private fun navigateTo(target: String) {
        controllersProvider.navController!!.navigate(target)
    }
}
