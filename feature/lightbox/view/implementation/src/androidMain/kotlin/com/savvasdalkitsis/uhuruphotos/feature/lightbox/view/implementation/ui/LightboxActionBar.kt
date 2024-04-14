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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ContributeToPortfolio
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.DownloadOriginal
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.SetFavourite
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UploadToServer
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.foundation.dismiss.api.ui.PullToDismissState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ToggleableActionIcon
import kotlinx.coroutines.launch

@Composable
fun RowScope.LightboxActionBar(
    state: LightboxState,
    index: Int,
    action: (LightboxAction) -> Unit,
    scrollState: ScrollState,
    dismissState: PullToDismissState,
) {
    val item = state.media[index]
    AnimatedVisibility(visible = state.isLoading) {
        if (state.isLoading) {
            LightboxDismissProgressAware(dismissState) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(26.dp)
                )
            }
        }
    }
    if (item.showAddToPortfolioIcon) {
        LightboxDismissProgressAware(dismissState) {
            ToggleableActionIcon(
                modifier = Modifier.alpha(
                    if (item.addToPortfolioIconEnabled) 1f else 0.7f
                ),
                onClick = {
                    action(ContributeToPortfolio(item, !item.inPortfolio))
                },
                enabled = item.addToPortfolioIconEnabled,
                icon = drawable.ic_feed,
                selected = item.inPortfolio,
                contentDescription = stringResource(string.show_on_feed)
            )
        }
    }
    item.mediaItemSyncState?.let { syncState ->
        LightboxDismissProgressAware(dismissState) {
            ActionIcon(
                modifier = Modifier.alpha(syncState.lightBoxIconAlpha),
                onClick = {
                    if (syncState == REMOTE_ONLY) {
                        action(DownloadOriginal(item))
                    }
                    if (syncState == LOCAL_ONLY) {
                        action(UploadToServer(item))
                    }
                },
                enabled = syncState.enabled,
                icon = syncState.lightBoxIcon,
                contentDescription = stringResource(syncState.contentDescription)
            )
        }
    }
    AnimatedVisibility(visible = item.showFavouriteIcon && item.isFavourite != null) {
        if (item.showFavouriteIcon && item.isFavourite != null) {
            LightboxDismissProgressAware(dismissState) {
                ActionIcon(
                    onClick = { action(SetFavourite(!item.isFavourite)) },
                    icon = if (item.isFavourite) drawable.ic_favourite else drawable.ic_not_favourite,
                    contentDescription = stringResource(
                        when {
                            item.isFavourite -> string.remove_favourite
                            else -> string.favourite
                        }
                    )
                )
            }
        }
    }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) {
        256.dp.toPx().toInt()
    }
    val showInfoButton by remember {
        derivedStateOf {
            scrollState.value < threshold
        }
    }
    AnimatedVisibility(visible = showInfoButton) {
        LightboxDismissProgressAware(dismissState) {
            ActionIcon(
                onClick = {
                    scope.launch {
                        scrollState.animateScrollTo(threshold + 1)
                    }
                },
                icon = drawable.ic_info,
                contentDescription = stringResource(string.info),
            )
        }
    }
}