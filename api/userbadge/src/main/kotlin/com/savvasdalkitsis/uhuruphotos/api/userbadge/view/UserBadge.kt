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
package com.savvasdalkitsis.uhuruphotos.api.userbadge.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.api.image.view.Image
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.SyncState.BAD
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.SyncState.GOOD
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.UserInformationState

@Composable
fun UserBadge(
    state: UserInformationState,
    userBadgePressed: (() -> Unit)? = null,
    size: Dp = 38.dp,
) {
    val backgroundColor = when (state.syncState) {
        BAD -> CustomColors.syncError
        GOOD -> CustomColors.syncSuccess
        IN_PROGRESS -> MaterialTheme.colors.background
    }
    Box(modifier = Modifier
        .let {
            when(userBadgePressed) {
                null -> it
                else -> it.clickable { userBadgePressed() }
            }
        }
        .clip(CircleShape)
        .background(backgroundColor)
        .size(size)
    ) {
        if (state.syncState == IN_PROGRESS) {
            CircularProgressIndicator(modifier = Modifier.size(size))
        }

        when {
            !state.avatarUrl.isNullOrEmpty() -> Image(
                modifier = Modifier
                    .size(size - 6.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                url = state.avatarUrl,
                contentScale = ContentScale.FillBounds,
                placeholder = ColorPainter(backgroundColor),
                contentDescription = "profileImage"
            )
            state.initials.isNotEmpty() -> Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = state.initials
            )
            else -> Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.ic_person),
                contentDescription = "profileIcon"
            )
        }
    }
}