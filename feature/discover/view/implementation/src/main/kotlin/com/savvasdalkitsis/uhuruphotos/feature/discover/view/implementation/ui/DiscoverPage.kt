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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewActionBar
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.AccountOverviewContent
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.viewmodel.DiscoverCompositeAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.viewmodel.DiscoverCompositeState
import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.ui.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.blurIf
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.discover

@Composable
internal fun DiscoverPage(
    state: DiscoverCompositeState,
    actions: (DiscoverCompositeAction) -> Unit,
) {
    DiscoverPage(
        state.first,
        isShowingPopUp = state.second.showAccountOverview,
        action = {
            actions(Either.Left(it))
        },
        actionBarContent = {
            AccountOverviewActionBar(state.second) {
                actions(Either.Right(it))
            }
        },
    ) {
        AccountOverviewContent(state.second) {
            actions(Either.Right(it))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiscoverPage(
    state: DiscoverState,
    isShowingPopUp: Boolean,
    action: (DiscoverAction) -> Unit,
    actionBarContent: @Composable () -> Unit,
    additionalContent: @Composable () -> Unit,
) {
    HomeScaffold(
        modifier = Modifier
            .blurIf(isShowingPopUp)
            .imeNestedScroll(),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Logo()
                Text(stringResource(string.discover))
            }
        },
        homeFeedDisplay = state.collageDisplayState,
        showLibrary = state.showLibrary,
        actionBarContent = {
            actionBarContent()
        }
    ) { contentPadding ->
        Discover(
            state = state,
            action = action,
            contentPadding = contentPadding
        )
        additionalContent()
    }
}