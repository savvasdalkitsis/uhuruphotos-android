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
package com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.navigation.FeedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.NotificationsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.ui.state.NotificationsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data object Skip : NotificationsAction() {
    context(NotificationsActionsContext)
    override fun handle(state: NotificationsState): Flow<Mutation<NotificationsState>> = flow {
        if (state.rememberChoice) {
            notificationsUseCase.neverShowNotificationsOnboardingScreenAgain()
        }
        navigator.newRoot(FeedNavigationRoute)
    }
}