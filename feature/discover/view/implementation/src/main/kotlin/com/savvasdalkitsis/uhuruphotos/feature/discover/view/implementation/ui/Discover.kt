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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DismissUpsellLogin
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DoNotShowUpsellAgain
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpsellDialog

@Composable fun Discover(
    state: DiscoverState,
    action: (DiscoverAction) -> Unit,
    contentPadding: PaddingValues,
) {
    Column {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
        SearchField(state, action)
        DiscoverIdle(state, action, contentPadding)
    }
    if (state.showLoginUpsellDialogFromPeople) {
        UpsellDialog(
            onDismiss = { action(DismissUpsellLogin) },
            onNeverAgain = { action(DoNotShowUpsellAgain) },
            onLogin = { action(Login) }
        )
    }
    if (state.showLoginUpsellDialogFromSearch) {
        UpsellDialog(
            onDismiss = { action(DismissUpsellLogin) },
            onLogin = { action(Login) }
        )
    }
}