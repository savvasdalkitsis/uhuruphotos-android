package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.center
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.datasource
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.mediaItemId
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.offsetFrom
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget.scale
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitEffect
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.Exhibit
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.ExhibitState
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.viewmodel.ExhibitViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ExhibitNavigationTarget @Inject constructor(
    private val effectsHandler: ExhibitEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<ExhibitState, ExhibitEffect, ExhibitAction, ExhibitViewModel>(
            name = ExhibitNavigationTarget.registrationName,
            effects = effectsHandler,
            themeMode = MutableStateFlow(ThemeMode.DARK_MODE),
            enterTransition = {
                slideIn(initialOffset = { fullSize ->
                    targetState.center.offsetFrom(fullSize)
                }) +
                        scaleIn(initialScale = targetState.scale) + fadeIn()
            },
            exitTransition = {
                slideOut(targetOffset = { fullSize ->
                    initialState.center.offsetFrom(fullSize)
                }) +
                        scaleOut(targetScale = initialState.scale) + fadeOut()
            },
            initializer = { navBackStackEntry, actions ->
                with(navBackStackEntry) {
                    actions(
                        ExhibitAction.LoadMediaItem(
                            mediaItemId,
                            isVideo,
                            datasource
                        )
                    )
                }
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Exhibit(state, actions)
        }
    }
}