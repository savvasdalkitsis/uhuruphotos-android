package com.savvasdalkitsis.uhuruphotos.foundation.effects.api.seam.effects

import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute

data class NavigateTo(val route: NavigationRoute) : CommonEffect() {
    context(CommonEffectsContext) override suspend fun handle() {
        navigator.navigateTo(route)
    }
}