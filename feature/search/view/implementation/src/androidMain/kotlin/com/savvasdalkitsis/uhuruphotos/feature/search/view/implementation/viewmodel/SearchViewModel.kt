/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.search.view.api.navigation.SearchNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.module.SearchModule
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchFor
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext

internal class SearchViewModel(
    actionsContext: SearchActionsContext = SearchModule.searchActionsContext,
) : NavigationViewModel<SearchState, SearchAction, SearchNavigationRoute>(
    ActionHandlerWithContext(actionsContext),
    SearchState(),
) {

    override fun onRouteSet(route: SearchNavigationRoute) {
        action(SearchFor(route.query))
    }
}