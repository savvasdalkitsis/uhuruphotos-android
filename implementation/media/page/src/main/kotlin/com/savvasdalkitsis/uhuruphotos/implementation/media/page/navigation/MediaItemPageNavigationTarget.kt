package com.savvasdalkitsis.uhuruphotos.implementation.media.page.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.center
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.datasource
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.isVideo
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.mediaItemId
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.offsetFrom
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget.scale
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.navigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageAction
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageEffect
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam.MediaItemPageEffectsHandler
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.MediaItemPage
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.MediaItemPageState
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.viewmodel.MediaItemPageViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MediaItemPageNavigationTarget @Inject constructor(
    private val effectsHandler: MediaItemPageEffectsHandler,
) : NavigationTarget {

    override suspend fun NavGraphBuilder.create(navHostController: NavHostController) {
        navigationTarget<MediaItemPageState, MediaItemPageEffect, MediaItemPageAction, MediaItemPageViewModel>(
            name = MediaItemPageNavigationTarget.registrationName,
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
                        MediaItemPageAction.LoadMediaItem(
                            mediaItemId,
                            isVideo,
                            datasource
                        )
                    )
                }
            },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            MediaItemPage(state, actions)
        }
    }
}