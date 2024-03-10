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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.DismissUpsellDialog
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.DoNotShowUpsellAgainFrom
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.Login
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpsellDialog

@Composable
internal fun Library(
    state: LibraryState,
    homeFeedDisplay: CollageDisplay,
    isShowingPopUp: Boolean,
    action: (LibraryAction) -> Unit,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    HomeScaffold(
        modifier = Modifier.blurIf(isShowingPopUp),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Logo()
                Text(stringResource(string.library))
            }
        },
        homeFeedDisplay = homeFeedDisplay,
        showLibrary = true,
        actionBarContent = {
           actionBarContent()
        },
    ) { contentPadding ->
        when {
            state.loading
                    && state.autoAlbums == null
                    && state.userAlbums == null -> FullLoading()
            else -> SwipeRefresh(
                indicatorPadding = contentPadding,
                isRefreshing = state.loading,
                onRefresh = { action(Refresh) }
            ) {
                LibraryGrid(contentPadding, state, action)
            }
        }
        additionalContent()
    }
    state.showUpsellDialogFromSource?.let { source ->
        UpsellDialog(
            onDismiss = { action(DismissUpsellDialog)},
            onNeverAgain = { action(DoNotShowUpsellAgainFrom(source)) },
        ) { action(Login) }
    }
}