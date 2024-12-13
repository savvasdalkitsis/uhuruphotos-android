package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.api.navigation.NotificationsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data object Continue : ThemeAction() {
    override fun ThemeActionsContext.handle(state: ThemeState): Flow<Mutation<ThemeState>> = flow {
        navigator.newRoot(when {
            notificationsUseCase.needToShowNotificationsOnboardingScreen() -> NotificationsNavigationRoute
            else -> FeedNavigationRoute
        })
    }
}