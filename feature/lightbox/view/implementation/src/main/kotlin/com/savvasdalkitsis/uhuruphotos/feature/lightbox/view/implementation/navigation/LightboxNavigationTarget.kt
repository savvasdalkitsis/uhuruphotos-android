package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.center
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.datasource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.mediaItemId
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.offsetFrom
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.scale
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget.showMediaSyncState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffect
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.LightboxEffectsHandler
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.Lightbox
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.viewmodel.LightboxViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class LightboxNavigationTarget @Inject constructor(
    private val effectsHandler: LightboxEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<LightboxState, LightboxEffect, LightboxAction, LightboxViewModel>(
            name = LightboxNavigationTarget.registrationName,
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
                        LightboxAction.LoadMediaItem(
                            mediaItemId,
                            isVideo,
                            datasource,
                            showMediaSyncState,
                        )
                    )
                }
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Lightbox(state, actions)
        }
    }
}