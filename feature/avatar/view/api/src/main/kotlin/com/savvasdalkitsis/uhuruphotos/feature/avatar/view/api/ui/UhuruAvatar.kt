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
package com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.BAD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.GOOD
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import org.jetbrains.compose.resources.painterResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_logo_small

@Composable
fun UhuruAvatar(
    state: AvatarState,
    avatarPressed: (() -> Unit)? = null,
    size: Dp = 38.dp,
) {
    val backgroundColor = when (state.syncState) {
        BAD -> MaterialTheme.colorScheme.error
        GOOD -> CustomColors.syncSuccess
        IN_PROGRESS -> MaterialTheme.colorScheme.background
    }
    val modifier = remember(avatarPressed == null, backgroundColor, size) {
        Modifier
            .recomposeHighlighter()
            .clip(CircleShape)
            .background(backgroundColor)
            .size(size)
            .let {
                when (avatarPressed) {
                    null -> it
                    else -> it.clickable { avatarPressed() }
                }
            }
            .padding(3.dp)
    }
    Box(modifier = modifier) {
        if (state.syncState == IN_PROGRESS) {
            CircularProgressIndicator(modifier = Modifier
                .recomposeHighlighter()
                .size(size))
        }

        when {
            !state.avatarUrl.isNullOrEmpty() -> Thumbnail(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .align(Alignment.Center),
                url = state.avatarUrl,
                respectNetworkCacheHeaders = true,
                contentScale = ContentScale.FillBounds,
                placeholder = backgroundColor.toArgb(),
                contentDescription = "profileImage"
            )
            state.initials.isNotEmpty() -> Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                text = state.initials,
                textAlign = TextAlign.Center,
            )
            else -> Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.Center),
                painter = painterResource(drawable.ic_logo_small),
                tint = Color.Black,
                contentDescription = "profileIcon"
            )
        }
    }
}

@Preview
@Composable
private fun AvatarPreview(@PreviewParameter(AvatarPreviews::class) state: AvatarState) {
    Column {
        PreviewAppTheme {
            UhuruAvatar(state = state)
        }
        PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
            UhuruAvatar(state = state)
        }
    }
}

private class AvatarPreviews : PreviewParameterProvider<AvatarState> {
    override val values: Sequence<AvatarState> = sequenceOf(
        AvatarState(
            initials = "SD",
            syncState = GOOD
        ),
        AvatarState(
            syncState = GOOD
        ),
        AvatarState(
            initials = "SD",
            syncState = BAD
        ),
        AvatarState(
            syncState = BAD
        ),
        AvatarState(
            initials = "SD",
            syncState = IN_PROGRESS
        ),
    )
}