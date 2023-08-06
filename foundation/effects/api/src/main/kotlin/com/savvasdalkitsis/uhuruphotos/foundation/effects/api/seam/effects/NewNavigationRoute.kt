package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute

data class NewNavigationRoute(val route: NavigationRoute) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        navigator.newRoot(route)
    }
}