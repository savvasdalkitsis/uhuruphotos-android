/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.navigation.UserAlbumsNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.Load
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.UserAlbumsAction
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserAlbumsViewModel @Inject constructor(
    userAlbumsActionsContext: UserAlbumsActionsContext,
) : NavigationViewModel<UserAlbumsState, UserAlbumsAction, UserAlbumsNavigationRoute>(
    ActionHandlerWithContext(userAlbumsActionsContext),
    UserAlbumsState(),
) {

    override fun onRouteSet(route: UserAlbumsNavigationRoute) {
        action(Load)
    }
}