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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DisableBatteryOptimizations
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.EnableCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.LogIn
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.LogOut
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.MemorySelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForDisablingBatteryOptimizations
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.NeverAskForLocalMediaAccessPermission
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.LocalMediaAccessRequestBanner
import com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui.RequestBanner
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.LoginButton
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.battery_optimizations_warning
import uhuruphotos_android.foundation.strings.api.generated.resources.connection_to_server_lost
import uhuruphotos_android.foundation.strings.api.generated.resources.disable
import uhuruphotos_android.foundation.strings.api.generated.resources.disable_battery_optimizations
import uhuruphotos_android.foundation.strings.api.generated.resources.enable
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_cloud_sync
import uhuruphotos_android.foundation.strings.api.generated.resources.forget_server
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_local_media_permissions

@Composable
fun SharedTransitionScope.FeedHeaders(
    state: FeedState,
    visible: Boolean,
    onScrollToMemory: (CelState) -> Unit,
    action: (FeedAction) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.memories.isNotEmpty()) {
                FeedMemories(state.memories, onScrollToMemory) { cel, yearsAgo ->
                    action(MemorySelected(cel, yearsAgo))
                }
            }
            val missingPermissions = state.showRequestPermissionForLocalMediaAccess
            if (missingPermissions != null) {
                LocalMediaAccessRequestBanner(
                    modifier = Modifier.padding(4.dp),
                    missingPermissions = missingPermissions,
                    description = string.missing_local_media_permissions,
                ) {
                    action(NeverAskForLocalMediaAccessPermission)
                }
            }
            if (state.showRequestForCloudSync) {
                RequestBanner(
                    modifier = Modifier.padding(4.dp),
                    description = string.enable_cloud_sync,
                    grantText = string.enable,
                    onAccessGranted = { action(EnableCloudSync) },
                    onNeverRemindMeAgain = { action(NeverAskForCloudSync) },
                )
            }
            if (state.showBatteryOptimizationBanner) {
                RequestBanner(
                    modifier = Modifier.padding(4.dp),
                    description = string.disable_battery_optimizations,
                    warning = string.battery_optimizations_warning,
                    grantText = string.disable,
                    onAccessGranted = { action(DisableBatteryOptimizations) },
                    onNeverRemindMeAgain = { action(NeverAskForDisablingBatteryOptimizations) },
                )
            }
            if (state.showLoginBanner) {
                RequestBanner(
                    modifier = Modifier.padding(4.dp),
                    description = string.connection_to_server_lost,
                    grantButton = {
                        LoginButton(
                            modifier = Modifier
                                .recomposeHighlighter()
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            action(LogIn)
                        }
                    },
                    denyButton = {
                        OutlinedButton(
                            modifier = Modifier
                                .recomposeHighlighter()
                                .fillMaxWidth()
                                .weight(1f),
                            onClick = { action(LogOut) },
                        ) {
                            Text(stringResource(string.forget_server))
                        }
                    },
                )
            }
            if (state.localMediaSyncRunning) {
                FeedLocalMediaSyncRunning()
            }
        }
    }
}