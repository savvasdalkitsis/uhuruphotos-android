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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeAnimateVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeMaxAnimatedVideoThumbnails
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun ColumnScope.SettingsVideo(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    SettingsCheckBox(
        text = stringResource(string.animate_video_thumbnails),
        icon = drawable.ic_play_filled,
        isChecked = state.animateVideoThumbnails,
    ) {
        action(ChangeAnimateVideoThumbnails(it))
    }
//    AnimatedVisibility(visible = state.animateVideoThumbnails) {
//        val range = 1f..5f
//        SettingsSliderRow(
//            text = { stringResource(string.max_animated_video_thumbnails, it.toInt()) },
//            subtext = string.max_animated_video_thumbnails_description,
//            initialValue = state.maxAnimatedVideoThumbnails.toFloat(),
//            range = range,
//            steps = (range.endInclusive - range.start - 1).toInt(),
//        ) {
//            action(ChangeMaxAnimatedVideoThumbnails(it.toInt()))
//        }
//    }
}