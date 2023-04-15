package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.Lightbox
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.viewmodel.LightboxViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTargetBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class LightboxNavigationTarget @Inject constructor(
    private val navigationTargetBuilder: NavigationTargetBuilder,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) = with(navigationTargetBuilder) {
        navigationTarget(
            themeMode = MutableStateFlow(ThemeMode.DARK_MODE),
            route = LightboxNavigationRoute::class,
            viewModel = LightboxViewModel::class,
        ) { state, actions ->
            Lightbox(state, actions)
        }
    }
}