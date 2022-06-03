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
package com.savvasdalkitsis.uhuruphotos.api.accountoverview.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.savvasdalkitsis.uhuruphotos.api.accountoverview.view.AccountOverview
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

@Composable
fun AccountOverviewPopUp(
    visible: Boolean,
    userInformationState: UserInformationState,
    onDismiss: () -> Unit,
    onLogoutClicked: () -> Unit,
    onEditServerClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Box {
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background.copy(alpha = 0.3f))
                    .clickable { onDismiss() }
            )
        }
        Popup(onDismissRequest = onDismiss) {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .align(Alignment.TopEnd),
                enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0f)),
                exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0f)),
                visible = visible,
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .widthIn(max = 480.dp)
                        .background(MaterialTheme.colors.background),
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row {
                        Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
                        IconButton(
                            modifier = Modifier.align(Alignment.Top),
                            onClick = onDismiss
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "close")
                        }
                    }
                    AccountOverview(
                        userInformationState,
                        onLogoutClicked,
                        onEditServerClicked,
                        onSettingsClicked,
                    )
                }
            }
        }
    }
}