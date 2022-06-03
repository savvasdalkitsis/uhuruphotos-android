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
package com.savvasdalkitsis.uhuruphotos.search.seam

import com.savvasdalkitsis.uhuruphotos.api.heatmap.navigation.HeatMapNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.homenavigation.HomeNavigationRoutes
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.people.navigation.PeopleNavigationTarget
import com.savvasdalkitsis.uhuruphotos.person.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.photos.model.PhotoSequenceDataSource.SearchResults
import com.savvasdalkitsis.uhuruphotos.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.ErrorRefreshingPeople
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.ErrorSearching
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.HideKeyboard
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.NavigateToAllPeople
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.NavigateToEditServer
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.NavigateToHeatMap
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.NavigateToSettings
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.search.seam.SearchEffect.ReloadApp
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.ui.usecase.UiUseCase
import javax.inject.Inject

class SearchEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val uiUseCase: UiUseCase,
    private val toaster: Toaster,
) : EffectHandler<SearchEffect> {

    override suspend fun handleEffect(
        effect: SearchEffect,
    ) = when (effect) {
        HideKeyboard -> uiUseCase.hideKeyboard()
        ReloadApp -> {
            with(navigator) {
                clearBackStack()
                navigateTo(HomeNavigationRoutes.home)
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
        navigator.navigateTo(target)
    }
}
