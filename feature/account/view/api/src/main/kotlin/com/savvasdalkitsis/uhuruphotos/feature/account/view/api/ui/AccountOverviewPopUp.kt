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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter

@Composable
fun AccountOverviewPopUp(
    state: AccountOverviewState,
    onDismiss: () -> Unit,
    onAboutClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onStartJob: (Job) -> Unit,
    onCancelJob: (Job) -> Unit,
    onViewAllUploadsClicked: () -> Unit = {},
) {
    val show = remember { MutableTransitionState(false) }
    show.targetState = state.showAccountOverview
    Box {
        if (show.currentState || show.targetState || !show.isIdle) {
            Box(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
                    .clickable { onDismiss() }
            )
            Popup(
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside= true,
                ),
                onDismissRequest = onDismiss,
            ) {
                AnimatedVisibility(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .padding(
                            top = 32.dp,
                            start = 16.dp,
                            end = 16.dp,
                        )
                        .align(Alignment.TopEnd),
                    enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0f)),
                    exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0f)),
                    visibleState = show
                ) {
                    Card(
                        modifier = Modifier
                            .recomposeHighlighter()
                            .widthIn(max = 480.dp),
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        AccountOverview(
                            state = state,
                            onAboutClicked = onAboutClicked,
                            onLogoutClicked = onLogoutClicked,
                            onLoginClicked = onLoginClicked,
                            onSettingsClicked = onSettingsClicked,
                            onStartJob = onStartJob,
                            onCancelJob = onCancelJob,
                            onClose = onDismiss,
                            onViewAllUploadsClicked = onViewAllUploadsClicked,
                        )
                    }
                }
            }
        }
    }
}