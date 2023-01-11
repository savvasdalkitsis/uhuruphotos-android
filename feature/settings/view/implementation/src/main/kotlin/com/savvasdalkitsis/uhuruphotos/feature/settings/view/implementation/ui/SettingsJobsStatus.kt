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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction.AskForFullFeedSync
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction.AskForPrecacheThumbnails
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction.CancelFullFeedSync
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsAction.CancelPrecacheThumbnails
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun ColumnScope.SettingsJobsStatus(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    AnimatedContent(targetState = state.fullSyncButtonEnabled) { enabled ->
        when {
            enabled -> SettingsButtonRow(
                buttonText = stringResource(string.perform_full_sync)
            ) {
                action(AskForFullFeedSync)
            }
            state.fullSyncJobProgress != null -> SettingsContentButtonRow(
                content = {
                    SettingsProgressIndicator(
                        text = stringResource(string.feed_sync_progress),
                        progress = state.fullSyncJobProgress,
                    )
                },
                buttonText = stringResource(string.cancel),
            ) {
                action(CancelFullFeedSync)
            }
            else -> SettingsButtonRow(
                enabled = false,
                buttonText = stringResource(string.cannot_perform_full_sync)
            ) {}
        }
    }
    AnimatedContent(targetState = state.precacheThumbnailsButtonEnabled) { enabled ->
        when {
            enabled -> SettingsButtonRow(
                buttonText = stringResource(string.precache_thumbnails),
                ) {
                    action(AskForPrecacheThumbnails)
                }
            state.precacheThumbnailsProgress != null -> SettingsContentButtonRow(
                content = {
                    SettingsProgressIndicator(
                        text = stringResource(string.precaching_thumbnails_progress),
                        progress = state.precacheThumbnailsProgress,
                    )
                },
                buttonText = stringResource(string.cancel),
            ) {
                action(CancelPrecacheThumbnails)
            }
            else -> SettingsButtonRow(
                enabled = false,
                buttonText = stringResource(string.cannot_perform_precaching),
            ) {}
        }
    }
}