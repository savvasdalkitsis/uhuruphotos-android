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
package com.savvasdalkitsis.uhuruphotos.implementation.localalbum.view

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.AlbumPage
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.seam.Either
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.ui.view.NoContent
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.seam.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.implementation.localalbum.view.state.LocalAlbumState
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
fun LocalAlbumPage(
    state: Pair<AlbumPageState, LocalAlbumState>,
    action: (Either<AlbumPageAction, LocalAlbumAction>) -> Unit
) {
    val permissionLauncher = rememberPermissionFlowRequestLauncher()

    AlbumPage(
        state = state.first,
        action = { action(Either.Left(it)) },
        emptyContent = {
            if (state.second.deniedPermissions.isNotEmpty()) {
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
                        Text(text = stringResource(R.string.missing_permissions))
                        Button(
                            onClick = {
                                permissionLauncher.launch(state.second.deniedPermissions.toTypedArray())
                            },
                        ) {
                            Text(text = stringResource(R.string.grant_permissions))
                        }
                    }
                }
            } else {
                NoContent(R.string.no_photos)
            }
        }
    )
}
