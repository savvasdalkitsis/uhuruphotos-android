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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.Collage
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.CollageDisplayActionButton
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.ChangeCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.ChangeGallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PeopleBanner
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton

@Composable
fun Gallery(
    state: GalleryState,
    additionalActionBarContent: @Composable RowScope.() -> Unit = {},
    emptyContent: @Composable () -> Unit = { NoContent(string.no_media) },
    action: (GalleryAction) -> Unit
) {
    CommonScaffold(
        title = { Text(state.title.toText()) },
        expandableTopBar = true,
        navigationIcon = { UpNavButton() },
        actionBarContent = {
            state.sorting?.let { sorting ->
                ActionIcon(
                    onClick = { action(ChangeGallerySorting(sorting.toggle())) },
                    icon = sorting.icon,
                    contentDescription = stringResource(string.sorting)
                )
            }
            AnimatedVisibility(state.collageState.collageDisplay.iconResource != 0
                    && state.collageState.clusters.isNotEmpty()) {
                CollageDisplayActionButton(
                    onChange = { action(ChangeCollageDisplay(it)) },
                    currentCollageDisplay = state.collageState.collageDisplay
                )
            }
            additionalActionBarContent()
        }
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            isRefreshing = state.collageState.isLoading,
            onRefresh = { action(SwipeToRefresh) }
        ) {
            Collage(
                contentPadding = contentPadding,
                state = state.collageState,
                collageHeader = state.people.takeIf { it.isNotEmpty() }?.let {
                    {
                        PeopleBanner(
                            modifier = Modifier.animateItemPlacement(),
                            people = state.people,
                            onPersonSelected = { action(PersonSelected(it)) }
                        )
                    }
                },
                emptyContent = emptyContent,
                onCelSelected = { cel ->
                    action(SelectedCel(cel))
                },
                onChangeDisplay = { action(ChangeCollageDisplay(it)) },
            )
        }
    }
}