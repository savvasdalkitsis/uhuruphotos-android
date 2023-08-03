package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.effects

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffectsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.api.navigation.SearchNavigationRoute

data class NavigateToSearch(val query: String) : LightboxEffect() {
    context(LightboxEffectsContext) override suspend fun handle() {
        navigator.navigateTo(SearchNavigationRoute(query))
    }
}