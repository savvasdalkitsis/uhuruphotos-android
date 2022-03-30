package com.savvasdalkitsis.librephotos.userbadge.view

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.savvasdalkitsis.librephotos.R
import com.savvasdalkitsis.librephotos.ui.theme.CustomColors
import com.savvasdalkitsis.librephotos.userbadge.view.state.SyncState.*
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

@Composable
fun UserBadge(
    state: UserBadgeState,
) {
    val size = 38.dp
    val backgroundColor = when (state.syncState) {
        BAD -> CustomColors.syncError
        GOOD -> CustomColors.syncSuccess
        IN_PROGRESS -> MaterialTheme.colors.background
    }
    Box(modifier = Modifier
        .clip(CircleShape)
        .background(backgroundColor)
        .size(size)
    ) {
        if (state.syncState == IN_PROGRESS) {
            CircularProgressIndicator(modifier = Modifier.size(size))
        }

        when {
            !state.avatarUrl.isNullOrEmpty() -> AsyncImage(
                modifier = Modifier
                    .size(size - 6.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(state.avatarUrl)
                    .crossfade(true)
                    .build(),
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