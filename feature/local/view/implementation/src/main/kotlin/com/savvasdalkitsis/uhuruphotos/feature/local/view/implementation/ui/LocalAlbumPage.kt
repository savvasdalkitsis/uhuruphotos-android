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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions.SetContributingToPortfolio
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.sharedElement
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruToggleableActionIcon
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_feed
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.grant_permissions
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_permissions
import uhuruphotos_android.foundation.strings.api.generated.resources.no_media

@Composable
fun SharedTransitionScope.LocalAlbumPage(
    state: Pair<GalleryState, LocalAlbumState>,
    action: (Either<GalleryAction, LocalAlbumAction>) -> Unit
) {
    val permissionLauncher = rememberPermissionFlowRequestLauncher()
    val galleryState = state.first
    val albumState = state.second
    val albumId = albumState.albumId

    Box(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        Gallery(
            modifier = Modifier
                .then(if (albumId != null) {
                    Modifier.sharedElement(SharedElementId.localAlbum(albumId))
                } else {
                    Modifier
                }),
            titleSharedElementId = albumId?.let { SharedElementId.localAlbumTitle(it) },
            state = galleryState,
            action = { action(Either.Left(it)) },
            additionalActionBarContent = {
                albumState.contributingToPortfolio?.let { contributing ->
                    UhuruToggleableActionIcon(
                        onClick = {
                            action(Either.Right(SetContributingToPortfolio(!contributing)))
                        },
                        icon = drawable.ic_feed,
                        selected = contributing,
                    )
                }
            },
            emptyContent = {
                if (albumState.deniedPermissions.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            verticalArrangement = spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = stringResource(string.missing_permissions))
                            Button(
                                onClick = {
                                    permissionLauncher.launch(albumState.deniedPermissions.toTypedArray())
                                },
                            ) {
                                Text(text = stringResource(string.grant_permissions))
                            }
                        }
                    }
                } else {
                    NoContent(string.no_media)
                }
            }
        )
    }
}
